package com.ingvarruulib.HeicAttachment;

import net.dv8tion.jda.api.entities.Message;
import org.im4java.core.ConvertCmd;
import org.im4java.core.IM4JavaException;
import org.im4java.core.IMOperation;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.Executor;

public final class HeicAttachment {
	private final Message.Attachment attachment;

	public HeicAttachment(Message.Attachment attachment) {
		this.attachment = attachment;
	}

	public CompletableFuture<Path> convertToPngAsync(Executor exec) {
		return simpleDownloadAsync().thenCompose(heic -> CompletableFuture.supplyAsync(() -> {
			Path png = heic.resolveSibling(stripExtension(heic.getFileName().toString()) + ".png");
			try {
				IMOperation op = new IMOperation();
				op.addImage(heic.toAbsolutePath().toString());
				op.addImage(png.toAbsolutePath().toString());


				new ConvertCmd().run(op);

				Files.deleteIfExists(heic);
				return png;
			} catch (Exception e) {
				try {
					Files.deleteIfExists(heic);
				} catch (IOException ignored) {
				}
				throw new CompletionException(e);
			}
		}, exec));
	}

	private static String stripExtension(String name) {
		int dot = name.lastIndexOf('.');
		return dot >= 0 ? name.substring(0, dot) : name;
	}

	public Path convertToPng() throws IOException, InterruptedException, IM4JavaException {
		Path heic = simpleDownload();
		if (heic == null) {
			return null;
		}

		Path png = heic.resolveSibling(heic.getFileName().toString() + ".png");

//		Files.createDirectories(png.getParent());

		IMOperation op = new IMOperation();
		op.addImage(heic.toAbsolutePath().toString());
		op.addImage(png.toAbsolutePath().toString());

		var cmd = new ConvertCmd();
		cmd.run(op);

		Files.deleteIfExists(heic);

		return png;
	}

	public CompletableFuture<Path> simpleDownloadAsync() {
		Path baseDir = Paths.get("img");
		try {
			Files.createDirectories(baseDir);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

		Path outPath = baseDir.resolve(attachment.getId() + ".heic").normalize();

		return attachment.getProxy().downloadToPath(outPath).thenApply(_ -> outPath);
	}

	public @Nullable Path simpleDownload() {
		Path baseDir = Paths.get("img");
		try {
			Files.createDirectories(baseDir);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

		Path outPath = baseDir.resolve(attachment.getId() + ".heic").normalize();

		try {
			attachment.getProxy().downloadToPath(outPath).join();
			return Files.exists(outPath) ? outPath : null;
		} catch (Exception e) {
			System.out.println("failed to download: " + outPath + ": " + e);

			return null;
		}
	}

	public Message.Attachment unwrap() {
		return attachment;
	}
}
