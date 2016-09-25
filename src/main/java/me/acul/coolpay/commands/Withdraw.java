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


public class Withdraw implements CommandExecutor {
    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        if (src instanceof Player) {
            Player ex = (Player) src;

            if (!Coolpay.configNode.getNode("disabled","withdraw").getBoolean()) {

                Optional<Integer> amount = args.getOne("amount");
                Optional<String> address = args.getOne("address");
                if (amount.isPresent() && address.isPresent()) {
                    Integer transfer = amount.get();
                    String to = address.get();
                    String uuid = ex.getUniqueId().toString();
                    Integer balance = Coolpay.rootNode.getNode("players", uuid, "balance").getInt();
                    if (transfer < 0) {
                        ex.sendMessage(Coolpay.getText(null, "text", "withdraw", "negative"));
                        return CommandResult.success();
                    }

                    if (balance >= transfer) {
                        int old = Coolpay.rootNode.getNode("floating", uuid, "out", to).getInt();
                        Coolpay.rootNode.getNode("floating", uuid, "out", to).setValue(old + transfer);
                        Coolpay.rootNode.getNode("players", uuid, "balance").setValue(balance - transfer);
                        Coolpay.saveConfig();
                        ex.sendMessage(Coolpay.getText(null, "text", "withdraw", "info"));
                    } else {
                        ex.sendMessage(Coolpay.getText(null, "text", "withdraw", "insufficient"));
                    }

                } else {
                    ex.sendMessage(Text.builder("[CoolPay] Usage: /withdraw <amount> <address>").color(TextColors.RED).build());
                }
            } else {
                ex.sendMessage(Coolpay.getText(null, "text","withdraw","disabled"));
            }
        }

        return CommandResult.success();
    }

}
