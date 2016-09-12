package me.acul.coolpay.Economy;

import org.spongepowered.api.service.context.Context;
import org.spongepowered.api.service.economy.Currency;
import org.spongepowered.api.service.economy.account.Account;
import org.spongepowered.api.service.economy.transaction.ResultType;
import org.spongepowered.api.service.economy.transaction.TransactionResult;
import org.spongepowered.api.service.economy.transaction.TransactionType;

import java.math.BigDecimal;
import java.util.Set;

/**
 * Created by Luca on 12.09.16.
 */
public class TransactionResultKrist implements TransactionResult {

    Object Account;
    BigDecimal Amount;

    public TransactionResultKrist(Object Acc, BigDecimal amt) {
        Account = Acc;
        Amount = amt;
    }

    @Override
    public Account getAccount() {
        return new UniqueAccountKrist(Account);
    }

    @Override
    public Currency getCurrency() {
        return new CurrencyKrist();
    }

    @Override
    public BigDecimal getAmount() {
        return Amount;
    }

    @Override
    public Set<Context> getContexts() {
        return null;
    }

    @Override
    public ResultType getResult() {
        return null;
    }

    @Override
    public TransactionType getType() {
        return null;
    }
}
