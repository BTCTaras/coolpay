package me.acul.coolpay;

import com.google.inject.Inject;
import me.acul.coolpay.commands.Balance;
import me.acul.coolpay.commands.Deposit;
import me.acul.coolpay.commands.Pay;
import me.acul.coolpay.commands.Withdraw;
import me.acul.coolpay.economy.EconomyServiceKrist;
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
import org.spongepowered.api.event.game.state.GamePreInitializationEvent;
import org.spongepowered.api.event.game.state.GameStartedServerEvent;
import org.spongepowered.api.event.network.ClientConnectionEvent;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.scheduler.Scheduler;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.service.economy.EconomyService;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.serializer.TextSerializers;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.security.SecureRandom;
import java.util.*;


@Plugin(id = "coolpay", name = "CoolPay", version = "1.0")
public class Coolpay {
    public static ConfigurationNode rootNode;
    public static ConfigurationNode configNode;
    public static int masterwallet;
    private static ConfigurationLoader<CommentedConfigurationNode> loader;
    private static ConfigurationLoader<CommentedConfigurationNode> configLoader;
    private static Formatter formatter;
    private final Scheduler scheduler = Sponge.getScheduler();
    @Inject
    private final Logger logger = null;
    @Inject
    @ConfigDir(sharedRoot = true)
    private final Path config = null;

    @SafeVarargs
    private static <E> Object[] unpack(E... objects) {
        List<Object> list = new ArrayList<>();
        for (Object object : objects) {
            if (object instanceof Object[]) {
                list.addAll(Arrays.asList((Object[]) object));
            }
            else{
                list.add(object);
            }
        }

        return list.toArray(new Object[list.size()]);
    }

    public static Text getText(String[] formatting, String... path) {
        if (formatting != null) {
            return TextSerializers.FORMATTING_CODE.deserialize(String.format(configNode.getNode(unpack(path)).getString(), unpack(formatting)));
        } else {
            return TextSerializers.FORMATTING_CODE.deserialize(configNode.getNode(unpack(path)).getString());
        }
    }

    public static String randomString() {

        SecureRandom random = new SecureRandom();
        StringBuilder sb = new StringBuilder();
        char[] set = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();
        for (int i = 0; i < 128; i++) {

            sb.append(set[random.nextInt(set.length)]);

        }

        return sb.toString();
    }

    public static void saveConfig() {
        try {
            loader.save(rootNode);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static String formatKST(int amt) {
        formatter.flush();
        formatter.format(Locale.ENGLISH, "%,d KST", amt);
        return formatter.toString();
    }

    @SuppressWarnings("UnusedParameters")
    @Listener
    public void onPreInit(GamePreInitializationEvent e) {
        Sponge.getServiceManager().setProvider(this, EconomyService.class, new EconomyServiceKrist());
    }

    @SuppressWarnings({"unused", "UnusedParameters"})
    @Listener
    public void onStart(GameStartedServerEvent event) {
        File co = new File(config.toString(), "coolpay_data.conf");
        File conf = new File(config.toString(), "coolpay.conf");
        logger.info("Initialising, please wait...");
        loader = HoconConfigurationLoader.builder().setPath(co.toPath()).build();
        configLoader = HoconConfigurationLoader.builder().setPath(conf.toPath()).build();
        formatter = new Formatter(new StringBuffer(), Locale.ENGLISH);
        try {

            configNode = configLoader.load();
            rootNode = loader.load();
            if (rootNode.getNode("masterpass").isVirtual()) {

                logger.info("Generating master password...");
                rootNode.getNode("Important information: ").setValue("DO NOT MODIFY ANYTHING IN THIS FILE!");
                String pass = randomString();
                rootNode.getNode("masterpass").setValue(pass);
                //Define defaults
                configNode.getNode("text","deposit","address").setValue("&a[CoolPay] Your deposit address: %s");
                configNode.getNode("text","deposit","transfer").setValue("&a[CoolPay] %s have been transferred to your account, %s still floating.");
                configNode.getNode("text","deposit","error").setValue("&4[CoolPay] Sorry! There has been a problem with your deposit, if this message keeps reappearing, please ask  an admin to help you! Error: %s");
                configNode.getNode("text","deposit","disabled").setValue("&4[CoolPay] Deposits have been disabled!");
                configNode.getNode("text","withdraw","info").setValue("&a[CoolPay] Withdraw added to queue, it can take up to 30 seconds until the first chunk is transferred");
                configNode.getNode("text","withdraw","negative").setValue("&4[CoolPay] You can't withdraw negative KST.");
                configNode.getNode("text","withdraw","insufficient").setValue("&4[CoolPay] You don't have enough KST");
                configNode.getNode("text","withdraw","transfer").setValue("&a[CoolPay] %s have been transferred to %s %s still floating.");
                configNode.getNode("text","withdraw","error").setValue("&4[CoolPay] There has been a problem with your transaction, if this message keeps reappearing please contact an admin! Error: %s");
                configNode.getNode("text","withdraw","disabled").setValue("&4[CoolPay] Withdraws have been disabled!");
                configNode.getNode("text","balance","info").setValue("&a[CoolPay] Your balance: %s");
                configNode.getNode("text","pay","negative").setValue("&4[CoolPay] You can't send negative KST");
                configNode.getNode("text","pay","insufficient").setValue("&4[CoolPay] You don't have enough KST");
                configNode.getNode("text","pay","sent").setValue("&a[CoolPay] Successfully send %s to %s");
                configNode.getNode("text","pay","received").setValue("&a[CoolPay] %s sent you %s");

                configNode.getNode("disabled","deposit").setValue(false);
                configNode.getNode("disabled","withdraw").setValue(false);

                configNode.getNode("refreshTime").setValue(30);

            }


            loader.save(rootNode);
            configLoader.save(configNode);

        } catch (IOException e) {

            logger.error(e.getMessage());

        }
        masterwallet = Krist.getBalance(Krist.makeV2Address(rootNode.getNode("masterpass").getString()));
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
        floating.intervalTicks(configNode.getNode("refreshTime").getInt()*20);
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
            saveConfig();
            Task.Builder taskBuilder = scheduler.createTaskBuilder();
            taskBuilder.delayTicks(1);
            taskBuilder.execute(() -> p.sendMessage(getText(new String[] {address},"text","deposit","address")));
            taskBuilder.submit(this);

        }

    }

}
