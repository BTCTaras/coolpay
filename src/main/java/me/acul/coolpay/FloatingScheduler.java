package me.acul.coolpay;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import java.util.Iterator;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;


class FloatingScheduler implements Runnable {

    /*
    //FIXME: stacktrace
    22:44:49 ERROR] [Sponge]: The Scheduler tried to run the task coolpay-S-0 owned by Plugin{id=coolpay, name=CoolPay, version=1.0, description=An example plugin, source=mods/coolpay-all-1.0-SNAPSHOT.jar}, but an error occured.
java.lang.NullPointerException
	at me.acul.coolpay.FloatingScheduler.run(FloatingScheduler.java:89) ~[FloatingScheduler.class:?]
	at org.spongepowered.api.scheduler.Task$Builder.lambda$execute$8(Task.java:138) ~[Task$Builder.class:1.8.9-4.2.0-BETA-350]
	at org.spongepowered.common.scheduler.SchedulerBase.lambda$startTask$291(SchedulerBase.java:177) ~[SchedulerBase.class:1.8.9-4.2.0-BETA-350]
	at org.spongepowered.common.scheduler.SyncScheduler.executeTaskRunnable(SyncScheduler.java:66) ~[SyncScheduler.class:1.8.9-4.2.0-BETA-350]
	at org.spongepowered.common.scheduler.SchedulerBase.startTask(SchedulerBase.java:174) ~[SchedulerBase.class:1.8.9-4.2.0-BETA-350]
	at org.spongepowered.common.scheduler.SchedulerBase.processTask(SchedulerBase.java:160) ~[SchedulerBase.class:1.8.9-4.2.0-BETA-350]
	at java.util.concurrent.ConcurrentHashMap$ValuesView.forEach(ConcurrentHashMap.java:4707) [?:1.8.0_71]
	at org.spongepowered.common.scheduler.SchedulerBase.runTick(SchedulerBase.java:104) [SchedulerBase.class:1.8.9-4.2.0-BETA-350]
	at org.spongepowered.common.scheduler.SyncScheduler.tick(SyncScheduler.java:41) [SyncScheduler.class:1.8.9-4.2.0-BETA-350]
	at org.spongepowered.common.scheduler.SpongeScheduler.tickSyncScheduler(SpongeScheduler.java:191) [SpongeScheduler.class:1.8.9-4.2.0-BETA-350]
	at net.minecraft.server.dedicated.DedicatedServer.handler$onTick$0(SourceFile:85) [ko.class:?]
	at net.minecraft.server.dedicated.DedicatedServer.func_71190_q(SourceFile:301) [ko.class:?]
	at net.minecraft.server.MinecraftServer.func_71217_p(SourceFile:535) [MinecraftServer.class:?]
	at net.minecraft.server.MinecraftServer.run(SourceFile:451) [MinecraftServer.class:?]
	at java.lang.Thread.run(Thread.java:745) [?:1.8.0_71]
     */

    @Override
    public void run() {
        //Fixme: This seems to do not work, I currently don't have time to investigate further.
        if (!Coolpay.rootNode.getNode("players").isVirtual()) {
            //Check if there's any money going in
            Map<String, Map> addresses = (Map) Coolpay.rootNode.getNode("players").getValue();
            Iterator it = addresses.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry pair = (Map.Entry) it.next();
                String uuid = (String) pair.getKey();
                String address = Krist.makeV2Address(Coolpay.rootNode.getNode("players", uuid, "pass").getString());
                int balance = Krist.getBalance(address);
                if (balance > 0) {
                    String master = Krist.makeV2Address(Coolpay.rootNode.getNode("masterpass").getString());
                    System.out.println("Transmitted " + balance + " from " + address + " to " + master);
                    TransactionResult res = Krist.transact(Coolpay.rootNode.getNode("players", uuid, "pass").getString(), master, balance);
                    if (!res.ok) {
                        Optional<Player> p = Sponge.getServer().getPlayer(UUID.fromString(uuid));
                        if (p.isPresent()) {
                            p.get().sendMessage(Text.builder("[CoolPay] Sorry! There has been a problem with your deposit, if this message keeps reappearing, please ask  an admin to help you! Error: " + res.error).color(TextColors.RED).build());
                        }

                    } else {
                        int old = Coolpay.rootNode.getNode("floating", uuid, "in").getInt();
                        Coolpay.rootNode.getNode("floating", uuid, "in").setValue(old + balance);
                    }

                }

                it.remove();
            }

        }

        Coolpay.saveConfig();
        if (!Coolpay.rootNode.getNode("floating").isVirtual()) {
            Map<String, Map> floating = (Map) Coolpay.rootNode.getNode("floating").getValue();
            for (Object o : floating.entrySet()) {
                Map.Entry pair = (Map.Entry) o;
                String uuid = (String) pair.getKey();
                int in = Coolpay.rootNode.getNode("floating", uuid, "in").getInt();
                if (in > 0) {

                    Optional<Player> p = Sponge.getServer().getPlayer(UUID.fromString(uuid));

                    if (in > 15000) {

                        int old = Coolpay.rootNode.getNode("players", uuid, "balance").getInt();
                        Coolpay.rootNode.getNode("players", uuid, "balance").setValue(old + 15000);
                        Coolpay.rootNode.getNode("floating", uuid, "in").setValue(in - 15000);
                        Coolpay.saveConfig();

                        if (p.isPresent()) {

                            p.get().sendMessage(Text.builder("[CoolPay] 15,000 KST have been transferred to your account, " + String.format("%,d KST", in - 15000) + " still floating.").color(TextColors.GREEN).build());

                        }

                    } else {

                        int old = Coolpay.rootNode.getNode("players", uuid, "balance").getInt();
                        Coolpay.rootNode.getNode("players", uuid, "balance").setValue(old + in);
                        Coolpay.rootNode.getNode("floating", uuid, "in").setValue(0);
                        Coolpay.saveConfig();

                        if (p.isPresent()) {

                            p.get().sendMessage(Text.builder("[CoolPay] " + String.format("%,d KST", in) + " have been transferred to your account. 0 KST still floating.").color(TextColors.GREEN).build());

                        }
                    }

                    Map<String, Integer> out = (Map) Coolpay.rootNode.getNode("floating", uuid, "out").getValue();

                    for (Object i : out.entrySet()) {

                        Map.Entry a = (Map.Entry) i;
                        String to = (String) a.getKey();
                        Integer amount = (Integer) a.getValue();

                        if (amount > 15000) {
                            TransactionResult res = Krist.transact(Coolpay.rootNode.getNode("masterpass").getString(), to, 15000);
                            if (res.ok) {

                                Coolpay.rootNode.getNode("floating", uuid, "out", to).setValue(amount - 15000);
                                Coolpay.saveConfig();

                                if (p.isPresent()) {

                                    p.get().sendMessage(Text.builder("[CoolPay] 15,000 KST have been transferred to " + to + " " + String.format("%,d KST", amount - 15000) + " still floating.").color(TextColors.GREEN).build());

                                }

                            }
                        } else {
                            TransactionResult res = Krist.transact(Coolpay.rootNode.getNode("masterpass").getString(), to, amount);
                            if (res.ok) {

                                Coolpay.rootNode.getNode("floating", uuid, "out", to).setValue(0);
                                Coolpay.saveConfig();

                                if (p.isPresent()) {

                                    p.get().sendMessage(Text.builder("[CoolPay] " + String.format("%,d KST", amount) + " have been transferred to " + to + " 0 KST still floating.").color(TextColors.GREEN).build());

                                }

                            } else {

                                if (p.isPresent()) {

                                    p.get().sendMessage(Text.builder("[CoolPay] There has been a problem with your transaction, if this message keeps reappearing please contact an admin! Error: " + res.error).color(TextColors.RED).build());

                                }
                            }

                        }
                    }
                }
            }
        }
    }
}