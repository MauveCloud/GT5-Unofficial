package gregtech.api.recipes;

import cpw.mods.fml.common.Loader;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import net.minecraftforge.fluids.FluidRegistry;

/**
 *
 * Maps the conditions that certain recipes might need to be enabled or disabled based on.
 * These are mostly boolean fields in other classes.<br>
 * Plus a few special cases:
 * <ul><li>ModLoaded(<i>ModName</i>) - Checks if the named mod is loaded
 * <li>ModsLoaded(<i>ModName</i>, <i>ModName</i>, ...) - Checks if all the mods in the comma separated list are loaded.
 * <li>FluidExists(<i>FluidName</i>) - Checks if the named fluid has been registered.
 * </ul>
 */
public class GT_RecipeConditions {
    private final static Map<String, Field> sConditionMap = new HashMap<>(100);
    
    private GT_RecipeConditions() {
        // Utility class
    }
    
    public static boolean getConditionValue(String aConditionName) {
        String tModLoadedPattern = "ModLoaded\\(([A-Za-z]+)\\)";
        String tModsLoadedPattern = "ModsLoaded\\(([A-Za-z, ]+)\\)";
        String tFluidExistsPattern = "FluidExists\\(([A-Za-z]+)\\)";
        if (aConditionName.matches(tModLoadedPattern)) {
            return Loader.isModLoaded(aConditionName.replaceFirst(tModLoadedPattern, "$1"));
        } else if (aConditionName.matches(tModsLoadedPattern)) {
            String[] tModNames = aConditionName.replaceFirst(tModsLoadedPattern, "$1").split(", *");
            for (String tModName : tModNames) {
                if (!Loader.isModLoaded(tModName)) {
                    return false;
                }
            }
            return true;
        } else if (aConditionName.matches(tFluidExistsPattern)) {
            return FluidRegistry.isFluidRegistered(aConditionName.replaceFirst(tFluidExistsPattern, "$1"));
        }
        try {
            return sConditionMap.get(aConditionName).getBoolean(null);
        } catch (IllegalArgumentException | IllegalAccessException e) {
            // ignore exceptions
        }
        return false;
    }
}
