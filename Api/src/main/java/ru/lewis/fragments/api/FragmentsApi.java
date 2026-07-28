package ru.lewis.fragments.api;

public class FragmentsApi {
    private static FragmentsApi INSTANCE;

    private final FragmentsEconomy fragmentsEconomy;

    public static void init(FragmentsEconomy fragmentsEconomy) {
        if (INSTANCE != null) return;
        INSTANCE = new FragmentsApi(fragmentsEconomy);
    }

    public FragmentsApi(FragmentsEconomy fragmentsEconomy) {
        this.fragmentsEconomy = fragmentsEconomy;
    }

    public FragmentsEconomy getFragmentsEconomy() {
        return fragmentsEconomy;
    }
}
