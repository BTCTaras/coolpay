package me.acul.coolpay.commands;

import me.acul.coolpay.Coolpay;
import me.acul.coolpay.Krist;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;


public class Deposit implements CommandExecutor {
    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        if (src instanceof Player) {
            Player p = (Player) src;
            String uuid = p.getUniqueId().toString();
            String pass = (String) Coolpay.rootNode.getNode("players", uuid, "pass").getValue();
            p.sendMessage(Coolpay.getText(new String[] {Krist.makeV2Address(pass)}, "text","deposit","address"));

        }

        return CommandResult.success();
    }

}
