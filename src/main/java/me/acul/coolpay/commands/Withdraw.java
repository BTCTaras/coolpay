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
    /* FIXME:Stacktrace:
             10:13:58 FATAL]: Error executing task
     java.util.concurrent.ExecutionException: java.lang.NullPointerException
     at java.util.concurrent.FutureTask.report(FutureTask.java:122) ~[?:1.8.0_91]
     at java.util.concurrent.FutureTask.get(FutureTask.java:192) ~[?:1.8.0_91]
     at net.minecraft.util.Util.func_181617_a(SourceFile:45) [g.class:?]
     at net.minecraft.server.MinecraftServer.func_71190_q(SourceFile:143) [MinecraftServer.class:?]
     at net.minecraft.server.dedicated.DedicatedServer.func_71190_q(SourceFile:299) [ko.class:?]
     at net.minecraft.server.MinecraftServer.func_71217_p(SourceFile:535) [MinecraftServer.class:?]
     at net.minecraft.server.MinecraftServer.run(SourceFile:451) [MinecraftServer.class:?]
     at java.lang.Thread.run(Thread.java:745) [?:1.8.0_91]
     Caused by: java.lang.NullPointerException
     at net.minecraft.command.ServerCommandManager.func_71556_a(SourceFile:81) ~[bd.class:?]
     at net.minecraft.network.NetHandlerPlayServer.func_147361_d(SourceFile:690) ~[lm.class:?]
     at net.minecraft.network.NetHandlerPlayServer.func_147354_a(SourceFile:677) ~[lm.class:?]
     at net.minecraft.network.play.client.C01PacketChatMessage.func_148833_a(SourceFile:37) ~[ie.class:?]
     at net.minecraft.network.play.client.C01PacketChatMessage.func_148833_a(SourceFile:9) ~[ie.class:?]
     at org.spongepowered.common.network.PacketUtil.onProcessPacket(PacketUtil.java:119) ~[PacketUtil.class:1.8.9-4.2.0-BETA-348]
     at net.minecraft.network.PacketThreadUtil$1.redirect$onProcessPacket$0(SourceFile:39) ~[fh$1.class:?]
     at net.minecraft.network.PacketThreadUtil$1.run(SourceFile:13) ~[fh$1.class:?]
     at java.util.concurrent.Executors$RunnableAdapter.call(Executors.java:511) ~[?:1.8.0_91]
     at java.util.concurrent.FutureTask.run(FutureTask.java:266) ~[?:1.8.0_91]
     at net.minecraft.util.Util.func_181617_a(SourceFile:44) ~[g.class:?]
             ... 5 more
 */
    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        if (src instanceof Player) {
            Player ex = (Player) src;
            Optional<Integer> amount = args.getOne("amount");
            Optional<String> address = args.getOne("address");
            if (amount.isPresent() && address.isPresent()) {
                Integer transfer = amount.get();
                String to = address.get();
                String uuid = ex.getUniqueId().toString();
                Integer balance = Coolpay.rootNode.getNode("players", uuid, "balance").getInt();
                if (transfer < 0) {
                    ex.sendMessage(Text.builder("[CoolPay] You can't withdraw negative KST.").color(TextColors.RED).build());
                    return null;
                }

                if (balance >= transfer) {
                    int old = Coolpay.rootNode.getNode("floating", uuid, "out", to).getInt();
                    Coolpay.rootNode.getNode("floating", uuid, "out", to).setValue(old + transfer);
                    Coolpay.rootNode.getNode("players", uuid, "balance").setValue(balance - transfer);
                    Coolpay.saveConfig();
                } else {
                    ex.sendMessage(Text.builder("[CoolPay] You don't have enough balance").color(TextColors.RED).build());
                }

            } else {
                ex.sendMessage(Text.builder("[CoolPay] Usage: /Withdraw <amount> <address>").color(TextColors.RED).build());
            }

        }

        return null;
    }

}
