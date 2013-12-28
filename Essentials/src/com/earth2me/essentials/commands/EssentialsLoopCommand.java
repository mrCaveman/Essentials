package com.earth2me.essentials.commands;

import com.earth2me.essentials.ChargeException;
import com.earth2me.essentials.CommandSource;
import com.earth2me.essentials.User;
import java.util.List;
import org.bukkit.Server;
import org.bukkit.entity.Player;

import static com.earth2me.essentials.I18n._;

public abstract class EssentialsLoopCommand extends EssentialsCommand
{
	public EssentialsLoopCommand(String command)
	{
		super(command);
	}

	protected void loopOfflinePlayers(final Server server, final CommandSource sender, final boolean multipleStringMatches, final String searchTerm, final String[] commandArgs)
			throws PlayerNotFoundException, NotEnoughArgumentsException, PlayerExemptException, ChargeException, Exception
	{
		if (searchTerm.isEmpty())
		{
			throw new PlayerNotFoundException();
		}

		if (searchTerm.contentEquals("**"))
		{
			if(!sender.isPlayer() || ess.getUser(sender.getPlayer()).isAuthorized("essentials." + getName() + ".wildcard"))
			{
				for (String sUser : ess.getUserMap().getAllUniqueUsers())
				{
					final User matchedUser = ess.getUser(sUser);
					updatePlayer(server, sender, matchedUser, commandArgs);
				}
			}
			else
			{
				throw new Exception(_("noAccessCommand"));
			}
		}
		else if (searchTerm.contentEquals("*"))
		{
			if (!sender.isPlayer() || ess.getUser(sender.getPlayer()).isAuthorized("essentials." + getName() + ".wildcard"))
			{
				boolean skipHidden = sender.isPlayer() && !ess.getUser(sender.getPlayer()).isAuthorized("essentials.vanish.interact");
				for (Player onlinePlayer : server.getOnlinePlayers())
				{
					final User onlineUser = ess.getUser(onlinePlayer);
					if (skipHidden && onlineUser.isHidden())
					{
						continue;
					}
					updatePlayer(server, sender, onlineUser, commandArgs);
				}
			}
			else
			{
				throw new Exception(_("noAccessCommand"));
			}
		}
		else if (multipleStringMatches)
		{
			if (searchTerm.trim().length() < 3)
			{
				throw new PlayerNotFoundException();
			}
			final List<Player> matchedPlayers = server.matchPlayer(searchTerm);
			if (matchedPlayers.isEmpty())
			{
				final User matchedUser = getPlayer(server, searchTerm, true, true);
				updatePlayer(server, sender, matchedUser, commandArgs);
			}
			for (Player matchPlayer : matchedPlayers)
			{
				final User matchedUser = ess.getUser(matchPlayer);
				updatePlayer(server, sender, matchedUser, commandArgs);
			}
		}
		else
		{
			final User user = getPlayer(server, searchTerm, true, true);
			updatePlayer(server, sender, user, commandArgs);
		}
	}

	protected void loopOnlinePlayers(final Server server, final CommandSource sender, final boolean multipleStringMatches, final String searchTerm, final String[] commandArgs)
			throws PlayerNotFoundException, NotEnoughArgumentsException, PlayerExemptException, ChargeException, Exception
	{
		if (searchTerm.isEmpty())
		{
			throw new PlayerNotFoundException();
		}

		boolean skipHidden = sender.isPlayer() && !ess.getUser(sender.getPlayer()).isAuthorized("essentials.vanish.interact");

		if (searchTerm.contentEquals("**") || searchTerm.contentEquals("*"))
		{
			if(!sender.isPlayer() || ess.getUser(sender.getPlayer()).isAuthorized("essentials." + getName() + ".wildcard"))
			{
				for (Player onlinePlayer : server.getOnlinePlayers())
				{
					final User onlineUser = ess.getUser(onlinePlayer);
					if (skipHidden && onlineUser.isHidden())
					{
						continue;
					}
					updatePlayer(server, sender, onlineUser, commandArgs);
				}
			}
			else
			{
				throw new Exception(_("noAccessCommand"));
			}
		}
		else if (multipleStringMatches)
		{
			if (searchTerm.trim().length() < 2)
			{
				throw new PlayerNotFoundException();
			}
			boolean foundUser = false;
			final List<Player> matchedPlayers = server.matchPlayer(searchTerm);
			for (Player matchPlayer : matchedPlayers)
			{
				final User player = ess.getUser(matchPlayer);
				if (skipHidden && player.isHidden())
				{
					continue;
				}
				foundUser = true;
				updatePlayer(server, sender, player, commandArgs);
			}
			if (!foundUser)
			{
				throw new PlayerNotFoundException();
			}
		}
		else
		{
			final User player = getPlayer(server, searchTerm, !skipHidden, false);
			updatePlayer(server, sender, player, commandArgs);
		}
	}

	protected abstract void updatePlayer(Server server, CommandSource sender, User user, String[] args)
			throws NotEnoughArgumentsException, PlayerExemptException, ChargeException;
}
