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
@SuppressWarnings("DefaultFileTemplate")
class TransactionResultKrist implements TransactionResult {

    private final Object Account;
    private final BigDecimal Amount;
    private final Set<Context> Contexts;
    private final ResultType Result;
    private final TransactionType Type;

    public TransactionResultKrist(Object Acc, BigDecimal amt, Set<Context> con, ResultType res, TransactionType t) {
        Account = Acc;
        Amount = amt;
        Contexts = con;
        Result = res;
        Type = t;
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
        return Contexts;
    }

    @Override
    public ResultType getResult() {
        return Result;
    }

    @Override
    public TransactionType getType() {
        return Type;
    }
}
