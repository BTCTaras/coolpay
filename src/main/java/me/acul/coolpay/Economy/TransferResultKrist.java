package me.acul.coolpay.Economy;

import org.spongepowered.api.service.context.Context;
import org.spongepowered.api.service.economy.Currency;
import org.spongepowered.api.service.economy.account.Account;
import org.spongepowered.api.service.economy.transaction.ResultType;
import org.spongepowered.api.service.economy.transaction.TransactionType;
import org.spongepowered.api.service.economy.transaction.TransferResult;

import java.math.BigDecimal;
import java.util.Set;

/**
 * Created by Luca on 14.09.16.
 */
public class TransferResultKrist implements TransferResult {

    Object to;
    Object from;
    Currency cur;
    BigDecimal amt;
    Set<Context> contexts;
    ResultType res;
    TransactionType trans;

    public TransferResultKrist(Object t, Object f, Currency c, BigDecimal a, Set<Context> co, ResultType r, TransactionType tt) {
        to = t;
        from = f;
        cur = c;
        amt = a;
        contexts = co;
        res = r;
        trans = tt;

    }

    @Override
    public Account getAccountTo() {
        return new UniqueAccountKrist(to);
    }

    @Override
    public Account getAccount() {
        return new UniqueAccountKrist(from);
    }

    @Override
    public Currency getCurrency() {
        return cur;
    }

    @Override
    public BigDecimal getAmount() {
        return amt;
    }

    @Override
    public Set<Context> getContexts() {
        return contexts;
    }

    @Override
    public ResultType getResult() {
        return res;
    }

    @Override
    public TransactionType getType() {
        return trans;
    }
}
