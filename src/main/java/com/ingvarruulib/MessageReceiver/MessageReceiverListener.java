package com.ingvarruulib.MessageReceiver;

import com.ingvarruulib.HeicAttachment.HeicAttachment;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.Channel;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.utils.AttachmentProxy;
import net.dv8tion.jda.api.utils.FileUpload;
import org.im4java.core.IM4JavaException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MessageReceiverListener extends ListenerAdapter {
	private static final ExecutorService EXEC =
			Executors.newFixedThreadPool(2);

	public static boolean isHeic(Message msg) {
		for (Message.Attachment attachment : msg.getAttachments()) {
			String ext = attachment.getFileExtension();
			if ("heic".equalsIgnoreCase(ext)) {
				return true;
			}
		}

		return false;
	}

	@Override
	public void onMessageReceived(MessageReceivedEvent event) {
		if (event.getAuthor().isBot()) {
			return;
		}

		Message message = event.getMessage();
		if (!isHeic(message)) {
			return;
		}

		MessageChannel channel = event.getChannel();

		for (Message.Attachment attachment : message.getAttachments()) {
			var heic = new HeicAttachment(attachment);

			heic.convertToPngAsync(EXEC)
					.thenAccept(png -> {
						try (FileUpload file = FileUpload.fromData(png)) {
							channel.sendFiles(file).queue(
									ok -> {
										try {
											Files.deleteIfExists(png);
										} catch (IOException ignored) {
										}
									},
									err -> {
										try {
											Files.deleteIfExists(png);
										} catch (IOException ignored) {
										}
									}
							);
						} catch (IOException e) {
							try {
								Files.deleteIfExists(png);
							} catch (IOException ignored2) {
							}
						}

					})
					.exceptionally(err -> {
						System.out.println("unable to process " + attachment.getFileName() + ": " + err);
						return null;
					});
		}

//		MessageChannel channel = event.getChannel();
//		channel.sendMessage("is heic!").queue(); // Important to call .queue() on the RestAction returned by sendMessage(...)
	}
}
