package me.acul.coolpay.economy;

import me.acul.coolpay.Coolpay;
import org.spongepowered.api.service.economy.Currency;
import org.spongepowered.api.text.Text;

import java.math.BigDecimal;

/**
 * Created by Luca on 12.09.16.
 */
@SuppressWarnings("DefaultFileTemplate")
class CurrencyKrist implements Currency {

    @Override
    public Text getDisplayName() {
        return Text.of("Krist");
    }

    @Override
    public Text getPluralDisplayName() {
        return getDisplayName();
    }

    @Override
    public Text getSymbol() {
        return Text.of("KST");
    }

    @Override
    public Text format(BigDecimal amount, int numFractionDigits) {
        return Text.of(Coolpay.formatKST(amount.intValue()));
    }

    @Override
    public int getDefaultFractionDigits() {
        return 0;
    }

    @Override
    public boolean isDefault() {
        return true;
    }

    @Override
    public String getId() {
        return "coolpay:kristcurrency";
    }

    @Override
    public String getName() {
        return "Krist";
    }
}
