package com.ingvarruulib;

import com.ingvarruulib.MessageReceiver.MessageReceiverListener;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.interactions.InteractionContextType;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.requests.restaction.CommandListUpdateAction;

import java.util.Collections;
import java.util.EnumSet;

import static net.dv8tion.jda.api.interactions.commands.OptionType.STRING;

public class Main {
	public static void main(String[] args) {
		if (args.length < 1 || args[0] == null || args[0].isBlank()) {
			System.err.println("Usage: java -jar <app>.jar <discord-bot-token>");
			System.exit(2);
		}
		
		JDABuilder.createLight(args[0], EnumSet.of(GatewayIntent.GUILD_MESSAGES, GatewayIntent.MESSAGE_CONTENT))
				.addEventListeners(new MessageReceiverListener())
				.build();
	}
}
