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

import java.util.Optional;


public class Pay implements CommandExecutor {

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        if (src instanceof Player) {
            Player ex = (Player) src;
            Optional<Integer> amount = args.getOne("amount");
            Optional<Player> t = args.getOne("to");
            if (amount.isPresent() && t.isPresent()) {
                Player to = t.get();
                Integer amt = amount.get();
                String fromUUID = ex.getUniqueId().toString();
                if (amt < 0) {
                    ex.sendMessage(Text.builder("[CoolPay] You can't send negative KST").color(TextColors.RED).build());
                    return CommandResult.success();
                }

                if (Coolpay.rootNode.getNode("players", fromUUID, "balance").getInt() >= amt) {
                    int old = Coolpay.rootNode.getNode("players", to.getUniqueId().toString(), "balance").getInt();
                    Coolpay.rootNode.getNode("players", to.getUniqueId().toString(), "balance").setValue(old + amt);
                    int oldf = Coolpay.rootNode.getNode("players", fromUUID, "balance").getInt();
                    Coolpay.rootNode.getNode("players", fromUUID, "balance").setValue(oldf - amt);
                    to.sendMessage(Text.builder("[CoolPay] " + ex.getName() + " sent you " + Coolpay.formatKST(amt) + " KST").color(TextColors.GREEN).build());
                    ex.sendMessage(Text.builder("[CoolPay] " + Coolpay.formatKST(amt) + " successfully sent to " + to.getName()).color(TextColors.GREEN).build());
                    Coolpay.saveConfig();
                } else {
                    ex.sendMessage(Text.builder("[CoolPay] You don't have enough KST").color(TextColors.RED).build());
                }

            } else {
                src.sendMessage(Text.builder("[CoolPay] Usage: /pay <player> <amount>").color(TextColors.RED).build());
            }

        }

        return CommandResult.success();
    }

}
