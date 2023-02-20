package com.thora.server.command;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.stream.Stream;

import com.thora.core.chat.CommandCaller;

public abstract class Command {
	
	public static final String EMPTY_MANUAL = "No provided manual";
	
	private final String name;
	private final Collection<String> names;
	private final Collection<String> aliases;
	private final String description;
	
	private final String manual;
	
	Command(final String name, final String description, final String manual, final Collection<String> aliases) {
		this.name = name;
		this.description = description;
		this.manual = manual;
		this.aliases = aliases;
		
		final Collection<String> tempNames = new LinkedHashSet<>();
		tempNames.add(name);
		tempNames.addAll(aliases);
		this.names = Collections.unmodifiableCollection(tempNames);
	}
	
	protected Command(final String name, final String description, final String manual, final String... aliases) {
		this(name, description, manual, Arrays.asList(aliases));
	}
	
	protected Command(final String name, final String description, final String... aliases) {
		this(name, description, EMPTY_MANUAL, Arrays.asList(aliases));
	}
	
	protected Command(final String name, final String... aliases) {
		this(name, null, aliases);
	}
	
	protected Command(final String name, final String description) {
		this(name, description, EMPTY_MANUAL, Collections.emptySet());
	}
	
	protected Command(final String name) {
		this(name, null, EMPTY_MANUAL, Collections.emptySet());
	}
	
	public final String getName() {
		return name;
	}
	
	public Collection<String> getNames() {
		return names;
	}
	
	public Stream<String> names() {
		return getNames().stream();
	}
	
	public Collection<String> getAliases() {
		return aliases;
	}
	
	public Stream<String> aliases() {
		return getAliases().stream();
	}
	
	public final String getDescription() {
		return description;
	}
	
	public String getManual() {
		return manual;
	}
	
	protected boolean execute(final CommandManager manager, final CommandCaller caller, final String text, final String cmd, final String[] args) {
		return this.execute(manager, caller, text, cmd, Arrays.asList(args));
	}
	
	protected abstract boolean execute(final CommandManager manager, final CommandCaller caller, final String text, final String cmd, final List<String> args);
	
}
