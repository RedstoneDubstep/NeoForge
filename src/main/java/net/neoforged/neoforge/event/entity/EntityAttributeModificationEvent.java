/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.neoforged.neoforge.event.entity;

import com.google.common.collect.ImmutableList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.DefaultAttributes;
import net.neoforged.bus.api.Event;
import net.neoforged.fml.event.IModBusEvent;

/**
 * EntityAttributeModificationEvent.<br>
 * Use this event to add attributes to existing entity types.
 * This event is fired after registration and before common setup, and after {@link EntityAttributeCreationEvent}
 * <br>
 * Fired on the Mod bus {@link IModBusEvent}.<br>
 **/
public class EntityAttributeModificationEvent extends Event implements IModBusEvent {
    private final Map<EntityType<? extends LivingEntity>, AttributeSupplier.Builder> entityAttributes;
    private final List<EntityType<? extends LivingEntity>> entityTypes;

    @SuppressWarnings("unchecked")
    public EntityAttributeModificationEvent(Map<EntityType<? extends LivingEntity>, AttributeSupplier.Builder> mapIn) {
        this.entityAttributes = mapIn;
        this.entityTypes = ImmutableList.copyOf(
                BuiltInRegistries.ENTITY_TYPE.stream()
                        .filter(DefaultAttributes::hasSupplier)
                        .map(entityType -> (EntityType<? extends LivingEntity>) entityType)
                        .collect(Collectors.toList()));
    }

    public void add(EntityType<? extends LivingEntity> entityType, Attribute attribute, double value) {
        AttributeSupplier.Builder attributes = entityAttributes.computeIfAbsent(entityType,
                (type) -> new AttributeSupplier.Builder());
        attributes.add(attribute, value);
    }

    public void add(EntityType<? extends LivingEntity> entityType, Attribute attribute) {
        add(entityType, attribute, attribute.getDefaultValue());
    }

    public boolean has(EntityType<? extends LivingEntity> entityType, Attribute attribute) {
        AttributeSupplier globalMap = DefaultAttributes.getSupplier(entityType);
        return globalMap.hasAttribute(attribute) || (entityAttributes.get(entityType) != null && entityAttributes.get(entityType).hasAttribute(attribute));
    }

    public List<EntityType<? extends LivingEntity>> getTypes() {
        return entityTypes;
    }

}
