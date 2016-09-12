package me.acul.coolpay.Economy;

import me.acul.coolpay.Coolpay;
import org.spongepowered.api.service.context.ContextCalculator;
import org.spongepowered.api.service.economy.Currency;
import org.spongepowered.api.service.economy.EconomyService;
import org.spongepowered.api.service.economy.account.Account;
import org.spongepowered.api.service.economy.account.UniqueAccount;

import java.util.*;

/**
 * Created by Luca on 12.09.16.
 */
@SuppressWarnings("DefaultFileTemplate")
class EconomyServiceKrist implements EconomyService{
    @Override
    public Currency getDefaultCurrency() {
        return new CurrencyKrist();
    }

    @Override
    public Set<Currency> getCurrencies() {
        return new HashSet<>(Collections.singletonList(getDefaultCurrency()));
    }

    @Override
    public boolean hasAccount(UUID uuid) {
        return Coolpay.rootNode.getNode("players", uuid.toString(), "balance").isVirtual();
    }

    @Override
    public boolean hasAccount(String identifier) {
        return Coolpay.rootNode.getNode("players", identifier, "balance").isVirtual();
    }

    @Override
    public Optional<UniqueAccount> getOrCreateAccount(UUID uuid) {
        return Optional.of(new UniqueAccountKrist(uuid));
    }

    @Override
    public Optional<Account> getOrCreateAccount(String identifier) {
        return Optional.of(new UniqueAccountKrist(identifier));
    }

    @Override
    public void registerContextCalculator(ContextCalculator<Account> calculator) {

    }
}
