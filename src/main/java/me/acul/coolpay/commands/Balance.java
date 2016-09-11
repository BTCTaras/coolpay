package me.acul.coolpay.commands;

import me.acul.coolpay.Coolpay;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;


public class Balance implements CommandExecutor {
    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        if (src instanceof Player) {
            Player p = (Player) src;
            String uuid = p.getUniqueId().toString();
            int balance = (int) Coolpay.rootNode.getNode("players", uuid, "balance").getValue();
            p.sendMessage(Text.builder("[CoolPay] Your balance: " + String.format("%,d KST", balance)).color(TextColors.GREEN).build());
        }

        return CommandResult.success();
    }
}