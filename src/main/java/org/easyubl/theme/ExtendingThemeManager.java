package org.easyubl.theme;

import org.clarksnut.common.Version;
import org.clarksnut.theme.ThemeProviderType.Type;
import org.jboss.logging.Logger;
import org.wildfly.swarm.spi.runtime.annotations.ConfigurationValue;

import javax.annotation.PostConstruct;
import javax.ejb.Lock;
import javax.ejb.LockType;
import javax.ejb.Singleton;
import javax.enterprise.inject.Any;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Singleton
@ThemeProviderType(type = Type.EXTENDING)
public class ExtendingThemeManager implements ThemeProvider {

    private static final Logger log = Logger.getLogger(ExtendingThemeManager.class);

    @Inject
    @ConfigurationValue("clarksnut.theme.default")
    private Optional<String> clarksnutDefaultTheme;

    @Inject
    @ConfigurationValue("clarksnut.theme.cacheThemes")
    private Optional<Boolean> clarksnutCacheThemes;

    @Inject
    @Any
    @ThemeManagerSelector
    private Instance<ThemeProvider> themeProviders;

    private String defaultTheme;
    private ConcurrentHashMap<ThemeKey, Theme> themeCache;
    private List<ThemeProvider> providers;

    @PostConstruct
    public void init() {
        defaultTheme = clarksnutDefaultTheme.orElse(Version.NAME.toLowerCase());
        if (clarksnutCacheThemes.orElse(true)) {
            themeCache = new ConcurrentHashMap<>();
        }
        loadProviders();
    }

    private void loadProviders() {
        providers = new LinkedList<>();
        for (ThemeProvider themeProvider : themeProviders) {
            providers.add(themeProvider);
        }
        providers.sort((o1, o2) -> o2.getProviderPriority() - o1.getProviderPriority());
    }

    @Override
    @Lock(LockType.READ)
    public int getProviderPriority() {
        return 0;
    }

    @Override
    @Lock(LockType.READ)
    public Theme getTheme(String name, Theme.Type type) throws IOException {
        if (name == null) {
            name = defaultTheme;
        }

        if (themeCache != null) {
            ThemeKey key = ThemeKey.get(name, type);
            Theme theme = themeCache.get(key);
            if (theme == null) {
                theme = loadTheme(name, type);
                if (theme == null) {
                    theme = loadTheme("clarksnut", type);
                    if (theme == null) {
                        theme = loadTheme("base", type);
                    }
                    log.errorv("Failed to find {0} theme {1}, using built-in themes", type, name);
                } else if (themeCache.putIfAbsent(key, theme) != null) {
                    theme = themeCache.get(key);
                }
            }
            return theme;
        } else {
            return loadTheme(name, type);
        }
    }

    private Theme loadTheme(String name, Theme.Type type) throws IOException {
        Theme theme = findTheme(name, type);
        if (theme != null && (theme.getParentName() != null || theme.getImportName() != null)) {
            List<Theme> themes = new LinkedList<>();
            themes.add(theme);

            if (theme.getImportName() != null) {
                String[] s = theme.getImportName().split("/");
                themes.add(findTheme(s[1], Theme.Type.valueOf(s[0].toUpperCase())));
            }

            if (theme.getParentName() != null) {
                for (String parentName = theme.getParentName(); parentName != null; parentName = theme.getParentName()) {
                    theme = findTheme(parentName, type);
                    themes.add(theme);

                    if (theme.getImportName() != null) {
                        String[] s = theme.getImportName().split("/");
                        themes.add(findTheme(s[1], Theme.Type.valueOf(s[0].toUpperCase())));
                    }
                }
            }

            return new ExtendingTheme(themes);
        } else {
            return theme;
        }
    }

    @Override
    @Lock(LockType.READ)
    public Set<String> nameSet(Theme.Type type) {
        Set<String> themes = new HashSet<>();
        for (ThemeProvider p : providers) {
            themes.addAll(p.nameSet(type));
        }
        return themes;
    }

    @Override
    @Lock(LockType.READ)
    public boolean hasTheme(String name, Theme.Type type) {
        for (ThemeProvider p : providers) {
            if (p.hasTheme(name, type)) {
                return true;
            }
        }
        return false;
    }

    private Theme findTheme(String name, Theme.Type type) {
        for (ThemeProvider p : providers) {
            if (p.hasTheme(name, type)) {
                try {
                    return p.getTheme(name, type);
                } catch (IOException e) {
                    log.errorv(e, p.getClass() + " failed to load theme, type={0}, name={1}", type, name);
                }
            }
        }
        return null;
    }

}
