/*
 * AndFHEM - Open Source Android application to control a FHEM home automation
 * server.
 *
 * Copyright (c) 2012, Matthias Klass or third-party contributors as
 * indicated by the @author tags or express copyright attribution
 * statements applied by the authors.  All third-party contributions are
 * distributed under license by Red Hat Inc.
 *
 * This copyrighted material is made available to anyone wishing to use, modify,
 * copy, or redistribute it subject to the terms and conditions of the GNU GENERAL PUBLICLICENSE, as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU GENERAL PUBLIC LICENSE
 * for more details.
 *
 * You should have received a copy of the GNU GENERAL PUBLIC LICENSE
 * along with this distribution; if not, write to:
 *   Free Software Foundation, Inc.
 *   51 Franklin Street, Fifth Floor
 */

package li.klass.fhem.billing;

import android.app.Activity;
import android.util.Log;
import li.klass.fhem.billing.amazon.AmazonBillingProvider;
import li.klass.fhem.billing.playstore.PlayStoreProvider;
import li.klass.fhem.util.ApplicationProperties;

import java.util.Set;

public class BillingService {
    public static final String BILLING_PROVIDER_PROPERTIES_KEY = "billing.provider";

    private enum ProviderType {
        AMAZON(AmazonBillingProvider.INSTANCE), GOOGLE(PlayStoreProvider.INSTANCE);

        private final BillingProvider storeProvider;

        ProviderType(BillingProvider storeProvider) {
            this.storeProvider = storeProvider;
        }

        public BillingProvider getStoreProvider() {
            return storeProvider;
        }
    }

    public static final BillingService INSTANCE = new BillingService();

    private BillingService() {
    }

    private ProviderType billingProvider;

    public boolean hasPendingRequestFor(String productId) {
        return getCurrentProvider().hasPendingRequestFor(productId);
    }

    public void bindActivity(Activity activity) {
        getCurrentProvider().bindActivity(activity);
    }

    public void unbindActivity(Activity activity) {
        getCurrentProvider().unbindActivity(activity);
    }

    public void markProductAsPurchases(String orderId, String productId,
                                       BillingConstants.PurchaseState purchaseState, long purchaseTime, String developerPayload) {
        PurchaseDatabase.INSTANCE.updatePurchase(orderId, productId, purchaseState, purchaseTime, developerPayload);
    }

    public void requestPurchase(String itemId, String payload) {
        getCurrentProvider().requestPurchase(itemId, payload);
    }

    public Set<String> getOwnedItems() {
        return PurchaseDatabase.INSTANCE.getOwnedItems();
    }

    public boolean isBillingSupported() {
        return getCurrentProvider().isBillingSupported();
    }

    private BillingProvider getCurrentProvider() {
        if (billingProvider == null) {
            try {
                String provider = ApplicationProperties.INSTANCE.getStringApplicationProperty(BILLING_PROVIDER_PROPERTIES_KEY);
                billingProvider = ProviderType.valueOf(provider);
            } catch (Exception e) {
                Log.e(BillingService.class.getName(), "cannot find billing provider property, falling back to Google");
                billingProvider = ProviderType.GOOGLE;
            }
            Log.e(BillingService.class.getName(), "set " + billingProvider.name() + " as billing provider!");
        }
        return billingProvider.getStoreProvider();
    }
}
