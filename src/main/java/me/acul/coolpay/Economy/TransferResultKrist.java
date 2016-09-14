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
    @Override
    public Account getAccountTo() {
        return null;
    }

    @Override
    public Account getAccount() {
        return null;
    }

    @Override
    public Currency getCurrency() {
        return null;
    }

    @Override
    public BigDecimal getAmount() {
        return null;
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
