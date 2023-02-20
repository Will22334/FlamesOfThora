package com.thora.server.command;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.google.common.collect.Sets;
import com.thora.core.Player;
import com.thora.core.chat.ChatFormatter;
import com.thora.core.chat.CommandCaller;
import com.thora.server.ServerPlayer;
import com.thora.server.ThoraServer;
import com.thora.server.world.ServerHashChunkWorld;

public class CommandListPlayers extends Command {
	
	public static final String NAME = "list";
	public static final String DESC = "List all players currently logged in.";
	public static final Set<String> aliases = Collections.unmodifiableSet(Sets.newLinkedHashSet(Arrays.asList("ls", "players", "all")));
	
	public final String deliminator = ", ";
	
	protected CommandListPlayers() {
		super(NAME, DESC, Command.EMPTY_MANUAL, aliases);
	}
	
	@Override
	protected boolean execute(final CommandManager manager, final CommandCaller caller, final String text, final String cmd,
			final List<String> args) {
		
		final ThoraServer server = manager.server();
		final ServerHashChunkWorld world = (ServerHashChunkWorld) server.getWorld();
		final Collection<ServerPlayer> players = world.getPlayers();
		final Iterator<ServerPlayer> it = world.players().iterator();
		final StringBuilder b = new StringBuilder();
		
		Player p;
		if(it.hasNext()) {
			p = it.next();
			b.append("Players: ");
			append(p, b);
		}
		
		while(it.hasNext()) {
			p = it.next();
			b.append(deliminator);
			append(p, b);
		}
		
		caller.sendMessage(b.toString());
		return true;
		
	}
	
	private void append(final Player player, final StringBuilder b) {
		b.append(ChatFormatter.color(206, 169, 18, 255));
		b.append(player.getName());
	}
	
}
