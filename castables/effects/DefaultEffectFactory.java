package org.shivas.core.core.castables.effects;

import com.google.common.collect.Maps;
import org.shivas.data.entity.SpellLevel;
import org.shivas.protocol.client.enums.SpellEffectTypeEnum;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Map;

import static java.util.Arrays.asList;
import static org.shivas.protocol.client.enums.SpellEffectTypeEnum.*;

/**
 * Created with IntelliJ IDEA.
 * User: Blackrush
 * Date: 29/10/12
 * Time: 10:14
 */
@Singleton
public class DefaultEffectFactory implements EffectFactory {
    private final Map<SpellEffectTypeEnum, EffectProvider> providers = Maps.newHashMap();

    @Override
    public EffectInterface create(SpellLevel level, SpellEffectTypeEnum effect) {
        EffectProvider provider = providers.get(effect);
        if (provider == null) return null;

        return provider.provide(level);
    }

    @Inject
    public void init() {
        for (SpellEffectTypeEnum type : asList(DamageNeutral, DamageEarth, DamageFire, DamageWind, DamageWater)) {
            providers.put(type, level -> new DamageEffect(level, type));
        }

        for (final SpellEffectTypeEnum type : asList(StealNeutral, StealEarth, StealFire, StealWind, StealWater)) {
            providers.put(type, level -> new StealEffect(level, type));
        }

        providers.put(Heal, HealEffect::new);
        providers.put(Teleport, TeleportEffect::new);
        providers.put(Transpose, TransposeEffect::new);
        providers.put(PushBack, PushEffect::new);
    }
}
