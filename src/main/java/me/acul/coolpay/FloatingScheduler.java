package me.acul.coolpay;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;

import java.util.Iterator;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;


class FloatingScheduler implements Runnable {

    @Override
    public void run() {
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
                    if (!Coolpay.configNode.getNode("disabled","deposit").getBoolean()) {
                        String master = Krist.makeV2Address(Coolpay.rootNode.getNode("masterpass").getString());
                        System.out.println("Transmitted " + balance + " from " + address + " to " + master);
                        TransactionResult res = Krist.transact(Coolpay.rootNode.getNode("players", uuid, "pass").getString(), master, balance);
                        if (!res.ok) {
                            Optional<Player> p = Sponge.getServer().getPlayer(UUID.fromString(uuid));
                            if (p.isPresent()) {
                                p.get().sendMessage(Coolpay.getText(new String[]{res.error}, "text", "deposit", "error"));
                            }

                        } else {
                            int old = Coolpay.rootNode.getNode("floating", uuid, "in").getInt();
                            Coolpay.rootNode.getNode("floating", uuid, "in").setValue(old + balance);
                            Coolpay.masterwallet = Coolpay.masterwallet + balance;
                        }
                    } else {
                        Optional<Player> p = Sponge.getServer().getPlayer(UUID.fromString(uuid));
                        if (p.isPresent()) {
                            p.get().sendMessage(Coolpay.getText(null, "text", "deposit", "disabled"));
                        }
                        Krist.transact(Coolpay.rootNode.getNode("masterpass").getString(), Krist.makeV2Address(Coolpay.rootNode.getNode("players", uuid, "pass").getString()), balance);
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
                Optional<Player> p = Sponge.getServer().getPlayer(UUID.fromString(uuid));
                if (in > 0) {

                    if (in > 15000) {

                        int old = Coolpay.rootNode.getNode("players", uuid, "balance").getInt();
                        Coolpay.rootNode.getNode("players", uuid, "balance").setValue(old + 15000);
                        Coolpay.rootNode.getNode("floating", uuid, "in").setValue(in - 15000);
                        Coolpay.saveConfig();

                        if (p.isPresent()) {

                            p.get().sendMessage(Coolpay.getText(new String[] {"15,000 KST", Coolpay.formatKST(in - 15000)}, "text","deposit","transfer"));

                        }

                    } else {

                        int old = Coolpay.rootNode.getNode("players", uuid, "balance").getInt();
                        Coolpay.rootNode.getNode("players", uuid, "balance").setValue(old + in);
                        Coolpay.rootNode.getNode("floating", uuid, "in").setValue(0);
                        Coolpay.saveConfig();

                        if (p.isPresent()) {

                            p.get().sendMessage(Coolpay.getText(new String[] {Coolpay.formatKST(in), "0 KST"}, "text","deposit","transfer"));

                        }
                    }
                }
                if (!Coolpay.rootNode.getNode("floating", uuid, "out").isVirtual()) {
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

                                Coolpay.masterwallet = Coolpay.masterwallet - amount;

                                if (p.isPresent()) {

                                    p.get().sendMessage(Coolpay.getText(new String[]{"15,000 KST", to, Coolpay.formatKST(amount-15000)}, "text","withdraw","transfer"));

                                }

                            }
                        } else if (amount > 0) {
                            TransactionResult res = Krist.transact(Coolpay.rootNode.getNode("masterpass").getString(), to, amount);
                            if (res.ok) {

                                Coolpay.rootNode.getNode("floating", uuid, "out", to).setValue(0);
                                Coolpay.saveConfig();

                                Coolpay.masterwallet = Coolpay.masterwallet - amount;

                                if (p.isPresent()) {

                                    p.get().sendMessage(Coolpay.getText(new String[]{Coolpay.formatKST(amount), to, "0 KST"}, "text","withdraw","transfer"));

                                }

                            } else {

                                if (p.isPresent()) {

                                    p.get().sendMessage(Coolpay.getText(new String[] {res.error}, "text","withdraw","error"));

                                }
                            }
                        }
                    }
                }
            }
        }
    }
}