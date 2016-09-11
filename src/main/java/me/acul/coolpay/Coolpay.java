package me.acul.coolpay;

import com.google.inject.Inject;
import me.acul.coolpay.commands.Balance;
import me.acul.coolpay.commands.Deposit;
import me.acul.coolpay.commands.Pay;
import me.acul.coolpay.commands.Withdraw;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import org.slf4j.Logger;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.config.ConfigDir;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GameStartedServerEvent;
import org.spongepowered.api.event.network.ClientConnectionEvent;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.scheduler.Scheduler;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.security.SecureRandom;
import java.util.Collections;
import java.util.Map;


@Plugin(id = "coolpay", name = "CoolPay", version = "1.0")
public class Coolpay {
    //TODO: Economy integration
    private String randomString() {

        SecureRandom random = new SecureRandom();
        StringBuilder sb = new StringBuilder();
        char[] set = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();
        for (int i = 0; i < 128; i++) {

            sb.append(set[random.nextInt(set.length)]);

        }

        return sb.toString();
    }

    private final Scheduler scheduler = Sponge.getScheduler();
    @Inject
    private final Logger logger = null;
    private static ConfigurationLoader<CommentedConfigurationNode> loader;
    @Inject
    @ConfigDir(sharedRoot = true)
    private final Path config = null;
    public static ConfigurationNode rootNode;

    public static void saveConfig() {
        try {
            loader.save(rootNode);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @SuppressWarnings({"unused", "UnusedParameters"})
    @Listener
    public void onStart(GameStartedServerEvent event) {
        File co = new File(config.toString(), "coolpay_data.conf");
        logger.info("Initialising, please wait...");
        loader = HoconConfigurationLoader.builder().setPath(co.toPath()).build();
        try {

            rootNode = loader.load();
            if (rootNode.getNode("masterpass").isVirtual()) {

                logger.info("Generating master password...");
                rootNode.getNode("Important information: ").setValue("DO NOT MODIFY ANYTHING IN THIS FILE!");
                String pass = randomString();
                rootNode.getNode("masterpass").setValue(pass);
                //TODO: Remove this after testing
                Map<String, String> addresses = Collections.emptyMap();
                rootNode.getNode("addresses").setValue(addresses);

            }

            loader.save(rootNode);

        } catch (IOException e) {

            logger.error(e.getMessage());

        }

        System.out.println(Krist.makeV2Address(rootNode.getNode("masterpass").getString()));

        CommandSpec a = CommandSpec.builder()
                .description(Text.of("Check your balance"))
                .executor(new Balance())
                .build();
        Sponge.getCommandManager().register(this, a, "balance");

        CommandSpec b = CommandSpec.builder()
                .description(Text.of("Check your deposit address"))
                .executor(new Deposit())
                .build();
        Sponge.getCommandManager().register(this, b, "deposit");

        CommandSpec c = CommandSpec.builder()
                .description(Text.of("Send a player money"))
                .arguments(
                        GenericArguments.onlyOne(GenericArguments.player(Text.of("to"))),
                        GenericArguments.onlyOne(GenericArguments.integer(Text.of("amount")))
                )
                .executor(new Pay())
                .build();
        Sponge.getCommandManager().register(this, c, "pay");

        CommandSpec d = CommandSpec.builder()
                .description(Text.of("Withdraw money"))
                .arguments(
                        GenericArguments.onlyOne(GenericArguments.integer(Text.of("amount"))),
                        GenericArguments.onlyOne(GenericArguments.string(Text.of("address")))
                )
                .executor(new Withdraw())
                .build();
        Sponge.getCommandManager().register(this, d, "withdraw");

        Task.Builder floating = scheduler.createTaskBuilder();
        floating.intervalTicks(600);
        floating.delayTicks(0);
        floating.execute(new FloatingScheduler());
        floating.submit(this);

    }

    @SuppressWarnings("unused")
    @Listener
    public void onJoin(ClientConnectionEvent.Join event) {

        Player p = event.getTargetEntity();
        String uuid = p.getUniqueId().toString();

        if (rootNode.getNode("players", uuid, "balance").isVirtual()) {

            String pass = randomString();
            String address = Krist.makeV2Address(pass);
            rootNode.getNode("players", uuid, "balance").setValue(0);
            rootNode.getNode("players", uuid, "pass").setValue(pass);
            //TODO: Remove this part after testing
            Map<String, String> addresses = (Map) rootNode.getNode("addresses").getValue();
            addresses.put(uuid, address);
            rootNode.getNode("addresses").setValue(addresses);
            saveConfig();
            Task.Builder taskBuilder = scheduler.createTaskBuilder();
            taskBuilder.delayTicks(1);
            taskBuilder.execute(() -> p.sendMessage(Text.builder("[CoolPay] Your Deposit address: " + address).color(TextColors.GREEN).build()));
            taskBuilder.submit(this);

        }

    }

}
