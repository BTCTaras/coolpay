package me.acul.coolpay.Economy;

import me.acul.coolpay.Coolpay;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.service.context.Context;
import org.spongepowered.api.service.economy.Currency;
import org.spongepowered.api.service.economy.account.Account;
import org.spongepowered.api.service.economy.account.UniqueAccount;
import org.spongepowered.api.service.economy.transaction.TransactionResult;
import org.spongepowered.api.service.economy.transaction.TransferResult;
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
        return null;
    }

    @Override
    public TransactionResult setBalance(Currency currency, BigDecimal amount, Cause cause) {
        return null;
    }

    @Override
    public Map<Currency, TransactionResult> resetBalances(Cause cause, Set<Context> contexts) {
        return null;
    }

    @Override
    public Map<Currency, TransactionResult> resetBalances(Cause cause) {
        return null;
    }

    @Override
    public TransactionResult resetBalance(Currency currency, Cause cause, Set<Context> contexts) {
        return null;
    }

    @Override
    public TransactionResult resetBalance(Currency currency, Cause cause) {
        return null;
    }

    @Override
    public TransactionResult deposit(Currency currency, BigDecimal amount, Cause cause, Set<Context> contexts) {
        return null;
    }

    @Override
    public TransactionResult deposit(Currency currency, BigDecimal amount, Cause cause) {
        return null;
    }

    @Override
    public TransactionResult withdraw(Currency currency, BigDecimal amount, Cause cause, Set<Context> contexts) {
        return null;
    }

    @Override
    public TransactionResult withdraw(Currency currency, BigDecimal amount, Cause cause) {
        return null;
    }

    @Override
    public TransferResult transfer(Account to, Currency currency, BigDecimal amount, Cause cause, Set<Context> contexts) {
        return null;
    }

    @Override
    public TransferResult transfer(Account to, Currency currency, BigDecimal amount, Cause cause) {
        return null;
    }

    @Override
    public String getIdentifier() {
        return null;
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
