package me.acul.coolpay.Economy;

import me.acul.coolpay.Coolpay;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.service.context.Context;
import org.spongepowered.api.service.economy.Currency;
import org.spongepowered.api.service.economy.account.Account;
import org.spongepowered.api.service.economy.account.UniqueAccount;
import org.spongepowered.api.service.economy.transaction.*;
import org.spongepowered.api.text.Text;

import java.math.BigDecimal;
import java.util.*;

/**
 * Created by Luca on 12.09.16.
 */

@SuppressWarnings("DefaultFileTemplate")
class UniqueAccountKrist implements UniqueAccount {

    private final Object uuid;

    public UniqueAccountKrist(Object id) {
        uuid = id;
        String uuidString;
        if (uuid instanceof String){
            uuidString = (String)uuid;
        } else {
            uuidString = uuid.toString();
        }
        if(Coolpay.rootNode.getNode("players",uuidString,"balance").isVirtual()){
            Coolpay.rootNode.getNode("players",uuidString,"balance").setValue(0);
            Coolpay.saveConfig();
        }
    }

    @Override
    public Text getDisplayName() {
        if (uuid instanceof UUID) {
            Optional<Player> p = Sponge.getServer().getPlayer((UUID)uuid);
            if (p.isPresent()) {
                return p.get().getDisplayNameData().displayName().get();
            }
            return null;
        } else {
            return Text.of((String)uuid);
        }
    }

    @Override
    public BigDecimal getDefaultBalance(Currency currency) {
        return BigDecimal.ZERO;
    }

    @Override
    public boolean hasBalance(Currency currency, Set<Context> contexts) {
        return true;
    }

    @Override
    public boolean hasBalance(Currency currency) {
        return true;
    }

    @Override
    public BigDecimal getBalance(Currency currency, Set<Context> contexts) {
        String uuidString;
        if (uuid instanceof String){
            uuidString = (String)uuid;
        } else {
            uuidString = uuid.toString();
        }
        if(currency.getId().equals("coolpay:kristcurrency")){
            return (BigDecimal) Coolpay.rootNode.getNode("players", uuidString, "balance").getValue();
        }
        return null;
    }

    @Override
    public BigDecimal getBalance(Currency currency) {
        String uuidString;
        if (uuid instanceof String){
            uuidString = (String)uuid;
        } else {
            uuidString = uuid.toString();
        }
        if(currency.getId().equals("coolpay:kristcurrency")){
            return (BigDecimal) Coolpay.rootNode.getNode("players", uuidString, "balance").getValue();
        }
        return null;
    }

    @Override
    public Map<Currency, BigDecimal> getBalances(Set<Context> contexts) {
        Map<Currency, BigDecimal> res = new HashMap<>();
        res.put(new CurrencyKrist(), getBalance(new CurrencyKrist()));
        return res;
    }

    @Override
    public Map<Currency, BigDecimal> getBalances() {
        Map<Currency, BigDecimal> res = new HashMap<>();
        res.put(new CurrencyKrist(), getBalance(new CurrencyKrist()));
        return res;
    }

    @Override
    public TransactionResult setBalance(Currency currency, BigDecimal amount, Cause cause, Set<Context> contexts) {
        String uuidString;
        if (uuid instanceof String){
            uuidString = (String)uuid;
        } else {
            uuidString = uuid.toString();
        }
        Coolpay.rootNode.getNode("players", uuidString, "balance").setValue(amount.intValue());
        Coolpay.saveConfig();
        return new TransactionResultKrist(uuid, amount, contexts, ResultType.SUCCESS, TransactionTypes.DEPOSIT);
    }

    @Override
    public TransactionResult setBalance(Currency currency, BigDecimal amount, Cause cause) {
        String uuidString;
        if (uuid instanceof String){
            uuidString = (String)uuid;
        } else {
            uuidString = uuid.toString();
        }
        int old = Coolpay.rootNode.getNode("players",uuidString,"balance").getInt();
        Coolpay.rootNode.getNode("players", uuidString, "balance").setValue(amount.intValue());
        Coolpay.saveConfig();
        TransactionType type;
        if (old > amount.intValue()){
            type = TransactionTypes.WITHDRAW;
        } else {
            type = TransactionTypes.DEPOSIT;
        }
        return new TransactionResultKrist(uuid, amount, null, ResultType.SUCCESS, type);
    }

    @Override
    public Map<Currency, TransactionResult> resetBalances(Cause cause, Set<Context> contexts) {
        String uuidString;
        if (uuid instanceof String){
            uuidString = (String)uuid;
        } else {
            uuidString = uuid.toString();
        }
        int old = Coolpay.rootNode.getNode("players",uuidString,"balance").getInt();
        Coolpay.rootNode.getNode("players", uuidString, "balance").setValue(getDefaultBalance(new CurrencyKrist()));
        Coolpay.saveConfig();
        Map<Currency,TransactionResult> res;
        res = new HashMap<>();
        res.put(new CurrencyKrist(), new TransactionResultKrist(uuid, new BigDecimal(old), contexts, ResultType.SUCCESS, TransactionTypes.WITHDRAW));
        return res;
    }

    @Override
    public Map<Currency, TransactionResult> resetBalances(Cause cause) {
        String uuidString;
        if (uuid instanceof String){
            uuidString = (String)uuid;
        } else {
            uuidString = uuid.toString();
        }
        int old = Coolpay.rootNode.getNode("players",uuidString,"balance").getInt();
        Coolpay.rootNode.getNode("players", uuidString, "balance").setValue(getDefaultBalance(new CurrencyKrist()));
        Coolpay.saveConfig();
        Map<Currency,TransactionResult> res;
        res = new HashMap<>();
        res.put(new CurrencyKrist(), new TransactionResultKrist(uuid, new BigDecimal(old), null, ResultType.SUCCESS, TransactionTypes.WITHDRAW));
        return res;
    }

    @Override
    public TransactionResult resetBalance(Currency currency, Cause cause, Set<Context> contexts) {
        String uuidString;
        if (uuid instanceof String){
            uuidString = (String)uuid;
        } else {
            uuidString = uuid.toString();
        }
        int old = Coolpay.rootNode.getNode("players",uuidString,"balance").getInt();
        Coolpay.rootNode.getNode("players", uuidString, "balance").setValue(getDefaultBalance(new CurrencyKrist()));
        Coolpay.saveConfig();
        return new TransactionResultKrist(uuid, new BigDecimal(old), contexts, ResultType.SUCCESS, TransactionTypes.WITHDRAW);
    }

    @Override
    public TransactionResult resetBalance(Currency currency, Cause cause) {
        String uuidString;
        if (uuid instanceof String){
            uuidString = (String)uuid;
        } else {
            uuidString = uuid.toString();
        }
        int old = Coolpay.rootNode.getNode("players",uuidString,"balance").getInt();
        Coolpay.rootNode.getNode("players", uuidString, "balance").setValue(getDefaultBalance(new CurrencyKrist()));
        Coolpay.saveConfig();
        return new TransactionResultKrist(uuid, new BigDecimal(old), null, ResultType.SUCCESS, TransactionTypes.WITHDRAW);
    }

    @Override
    public TransactionResult deposit(Currency currency, BigDecimal amount, Cause cause, Set<Context> contexts) {
        String uuidString;
        if (uuid instanceof String){
            uuidString = (String)uuid;
        } else {
            uuidString = uuid.toString();
        }
        int old = Coolpay.rootNode.getNode("players",uuidString,"balance").getInt();
        Coolpay.rootNode.getNode("players", uuidString, "balance").setValue(old + amount.intValue());
        Coolpay.saveConfig();
        return new TransactionResultKrist(uuid, amount, contexts, ResultType.SUCCESS, TransactionTypes.DEPOSIT);
    }

    @Override
    public TransactionResult deposit(Currency currency, BigDecimal amount, Cause cause) {
        String uuidString;
        if (uuid instanceof String){
            uuidString = (String)uuid;
        } else {
            uuidString = uuid.toString();
        }
        int old = Coolpay.rootNode.getNode("players",uuidString,"balance").getInt();
        Coolpay.rootNode.getNode("players", uuidString, "balance").setValue(old + amount.intValue());
        Coolpay.saveConfig();
        return new TransactionResultKrist(uuid, amount, null, ResultType.SUCCESS, TransactionTypes.DEPOSIT);
    }

    @Override
    public TransactionResult withdraw(Currency currency, BigDecimal amount, Cause cause, Set<Context> contexts) {
        String uuidString;
        if (uuid instanceof String){
            uuidString = (String)uuid;
        } else {
            uuidString = uuid.toString();
        }
        int old = Coolpay.rootNode.getNode("players",uuidString,"balance").getInt();
        if (old >= amount.intValue()) {
            Coolpay.rootNode.getNode("players", uuidString, "balance").setValue(old - amount.intValue());
            Coolpay.saveConfig();
            return new TransactionResultKrist(uuid, amount, contexts, ResultType.SUCCESS, TransactionTypes.WITHDRAW);
        } else {
            return new TransactionResultKrist(uuid, amount, contexts, ResultType.ACCOUNT_NO_FUNDS, TransactionTypes.WITHDRAW);
        }
    }

    @Override
    public TransactionResult withdraw(Currency currency, BigDecimal amount, Cause cause) {
        String uuidString;
        if (uuid instanceof String){
            uuidString = (String)uuid;
        } else {
            uuidString = uuid.toString();
        }
        int old = Coolpay.rootNode.getNode("players",uuidString,"balance").getInt();
        if (old >= amount.intValue()) {
            Coolpay.rootNode.getNode("players", uuidString, "balance").setValue(old - amount.intValue());
            Coolpay.saveConfig();
            return new TransactionResultKrist(uuid, amount, null, ResultType.SUCCESS, TransactionTypes.WITHDRAW);
        } else {
            return new TransactionResultKrist(uuid, amount, null, ResultType.ACCOUNT_NO_FUNDS, TransactionTypes.WITHDRAW);
        }
    }

    @Override
    public TransferResult transfer (Account to, Currency currency, BigDecimal amount, Cause cause, Set<Context> contexts) {
        String uuidStringFrom;
        if (uuid instanceof String){
            uuidStringFrom = (String)uuid;
        } else {
            uuidStringFrom = uuid.toString();
        }
        String uuidStringTo = to.getIdentifier();
        int oldFrom = Coolpay.rootNode.getNode("players",uuidStringFrom,"balance").getInt();
        int oldTo = Coolpay.rootNode.getNode("players",uuidStringTo,"balance").getInt();
        if (oldFrom >= amount.intValue()){
            Coolpay.rootNode.getNode("players",uuidStringTo,"balance").setValue(oldTo + amount.intValue());
            Coolpay.rootNode.getNode("players",uuidStringFrom, "balance").setValue(oldFrom - amount.intValue());
            Coolpay.saveConfig();
            return new TransferResultKrist(uuidStringTo, uuidStringFrom, new CurrencyKrist(), amount, contexts, ResultType.SUCCESS, TransactionTypes.TRANSFER);
        } else {
            return new TransferResultKrist(uuidStringTo, uuidStringFrom, new CurrencyKrist(), amount, contexts, ResultType.ACCOUNT_NO_FUNDS, TransactionTypes.TRANSFER);
        }
    }

    @Override
    public TransferResult transfer(Account to, Currency currency, BigDecimal amount, Cause cause) {
        String uuidStringFrom;
        if (uuid instanceof String){
            uuidStringFrom = (String)uuid;
        } else {
            uuidStringFrom = uuid.toString();
        }
        String uuidStringTo = to.getIdentifier();
        int oldFrom = Coolpay.rootNode.getNode("players",uuidStringFrom,"balance").getInt();
        int oldTo = Coolpay.rootNode.getNode("players",uuidStringTo,"balance").getInt();
        if (oldFrom >= amount.intValue()){
            Coolpay.rootNode.getNode("players",uuidStringTo,"balance").setValue(oldTo + amount.intValue());
            Coolpay.rootNode.getNode("players",uuidStringFrom, "balance").setValue(oldFrom - amount.intValue());
            Coolpay.saveConfig();
            return new TransferResultKrist(uuidStringTo, uuidStringFrom, new CurrencyKrist(), amount, null, ResultType.SUCCESS, TransactionTypes.TRANSFER);
        } else {
            return new TransferResultKrist(uuidStringTo, uuidStringFrom, new CurrencyKrist(), amount, null, ResultType.ACCOUNT_NO_FUNDS, TransactionTypes.TRANSFER);
        }
    }

    @Override
    public String getIdentifier() {
        String uuidString;
        if (uuid instanceof String){
            uuidString = (String)uuid;
        } else {
            uuidString = uuid.toString();
        }
        return uuidString;
    }

    @Override
    public Set<Context> getActiveContexts() {
        return null;
    }

    @Override
    public UUID getUniqueId() {
        if(uuid instanceof UUID) {
            return (UUID)uuid;
        } else {
            return UUID.fromString((String)uuid);
        }

    }
}
