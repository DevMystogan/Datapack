package handlers.effecthandlers;

import org.l2j.commons.util.Rnd;
import org.l2j.commons.util.Util;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.handler.TargetHandler;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.effects.AbstractEffect;
import org.l2j.gameserver.model.events.EventType;
import org.l2j.gameserver.model.events.impl.character.OnCreatureSkillFinishCast;
import org.l2j.gameserver.model.events.listeners.ConsumerEventListener;
import org.l2j.gameserver.model.holders.SkillHolder;
import org.l2j.gameserver.model.items.instance.Item;
import org.l2j.gameserver.model.skills.SkillCaster;
import org.l2j.gameserver.model.skills.targets.TargetType;

import java.util.function.Consumer;

import static org.l2j.gameserver.util.GameUtils.isCreature;

/**
 * Trigger skill by isMagic type.
 * @author Nik
 */
public final class TriggerSkillByMagicType extends AbstractEffect {
	public final int[] magicTypes;
	public final int chance;
	public final SkillHolder skill;
	public final TargetType targetType;

	public TriggerSkillByMagicType(StatsSet params) {
		chance = params.getInt("chance", 100);
		magicTypes = params.getIntArray("types", " ");
		skill = new SkillHolder(params.getInt("skill", 0), params.getInt("power", 0));
		targetType = params.getEnum("target", TargetType.class, TargetType.TARGET);
	}
	
	private void onSkillUseEvent(OnCreatureSkillFinishCast event) {
		if (!isCreature(event.getTarget())) {
			return;
		}
		
		if (!Util.contains(magicTypes, event.getSkill().getMagicType())) {
			return;
		}
		
		if (Rnd.chance(chance)) {
			return;
		}
		
		final Skill triggerSkill = skill.getSkill();
		
		WorldObject target = null;
		try {
			target = TargetHandler.getInstance().getHandler(targetType).getTarget(event.getCaster(), event.getTarget(), triggerSkill, false, false, false);
		} catch (Exception e) {
			LOGGER.warn("Exception in ITargetTypeHandler.getTarget(): " + e.getMessage(), e);
		}
		
		if (isCreature(target)) {
			SkillCaster.triggerCast(event.getCaster(), (Creature) target, triggerSkill);
		}
	}
	
	@Override
	public void onStart(Creature effector, Creature effected, Skill skill, Item item) {
		if (chance == 0|| this.skill.getSkillId() == 0 || this.skill.getLevel() == 0 || magicTypes.length == 0) {
			return;
		}
		
		effected.addListener(new ConsumerEventListener(effected, EventType.ON_CREATURE_SKILL_FINISH_CAST, (Consumer<OnCreatureSkillFinishCast>) this::onSkillUseEvent, this));
	}
	
	@Override
	public void onExit(Creature effector, Creature effected, Skill skill)
	{
		effected.removeListenerIf(EventType.ON_CREATURE_SKILL_FINISH_CAST, listener -> listener.getOwner() == this);
	}
}
