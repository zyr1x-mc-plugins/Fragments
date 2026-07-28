package ru.lewis.fragments.api;

public record FragmentsApi(FragmentsEconomy fragmentsEconomy) {
    private static FragmentsApi INSTANCE;

    public static FragmentsApi get() {
        return INSTANCE;
    }

    public static void init(FragmentsEconomy fragmentsEconomy) {
        if (INSTANCE != null) return;
        INSTANCE = new FragmentsApi(fragmentsEconomy);
    }
}
