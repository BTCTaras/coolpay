package me.acul.coolpay.economy;

import org.spongepowered.api.service.context.Context;
import org.spongepowered.api.service.economy.Currency;
import org.spongepowered.api.service.economy.account.Account;
import org.spongepowered.api.service.economy.transaction.ResultType;
import org.spongepowered.api.service.economy.transaction.TransactionType;
import org.spongepowered.api.service.economy.transaction.TransactionTypes;
import org.spongepowered.api.service.economy.transaction.TransferResult;

import java.math.BigDecimal;
import java.util.Set;

/**
 * Created by Luca on 14.09.16.
 */
@SuppressWarnings({"unused", "DefaultFileTemplate"})
class TransferResultKrist implements TransferResult {

    private final Object to;
    private final Object from;
    private final Currency cur;
    private final BigDecimal amt;
    private final Set<Context> contexts;
    private final ResultType res;
    private final TransactionType trans;

    public TransferResultKrist(Object t, Object f, Currency c, BigDecimal a, Set<Context> co, ResultType r) {
        to = t;
        from = f;
        cur = c;
        amt = a;
        contexts = co;
        res = r;
        trans = TransactionTypes.TRANSFER;

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
