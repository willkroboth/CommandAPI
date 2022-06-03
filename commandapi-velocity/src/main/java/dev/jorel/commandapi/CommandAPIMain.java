/*******************************************************************************
 * Copyright 2022 Jorel Ali (Skepter) - MIT License
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of
 * the Software, and to permit persons to whom the Software is furnished to do so,
 * subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS
 * FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
 * IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *******************************************************************************/
package dev.jorel.commandapi;

import org.slf4j.Logger;

import com.google.inject.Inject;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.proxy.ProxyServer;


@Plugin(id = "commandapi-velocity",
name = "CommandAPI",
version = "8.2.0",
url = "https://commandapi.jorel.dev",
description = "An API to use Minecraft 1.13s new command UI",
authors = {"Skepter"})
public class CommandAPIMain {

	private final ProxyServer server;
	private final Logger logger;

	@Inject
	public CommandAPIMain(ProxyServer server, Logger logger) {
		this.server = server;
		this.logger = logger;
	}

	@Subscribe
	public void onProxyInitialization(ProxyInitializeEvent event) {
		// So you're probably wondering, why does this commit have basically
		// no content? Well the reason for that is because I actually have
		// absolutely no idea where to even start with adding Velocity support!
		
//		new CommandAPICommand("mycommand")
//		.executes((sender, args) -> {
//			
//		});
	}
	
	public void executes(CommandSource sender, Object[] args) {
//		PlayerArgument pArg = new PlayerArgument("hi");
	}
}
