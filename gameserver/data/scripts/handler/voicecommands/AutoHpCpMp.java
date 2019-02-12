package handler.voicecommands;

import org.l2j.gameserver.Config;
import org.l2j.gameserver.listener.actor.OnChangeCurrentCpListener;
import org.l2j.gameserver.listener.actor.OnChangeCurrentHpListener;
import org.l2j.gameserver.listener.actor.OnChangeCurrentMpListener;
import org.l2j.gameserver.listener.actor.player.OnPlayerEnterListener;
import org.l2j.gameserver.model.Creature;
import org.l2j.gameserver.model.Player;
import org.l2j.gameserver.model.Skill;
import org.l2j.gameserver.model.actor.instances.creature.Abnormal;
import org.l2j.gameserver.model.actor.listener.CharListenerList;
import org.l2j.gameserver.model.items.ItemInstance;
import org.l2j.gameserver.skills.EffectType;
import org.l2j.gameserver.skills.EffectUseType;
import org.l2j.gameserver.skills.SkillEntry;
import org.l2j.gameserver.skills.effects.Effect;
import org.l2j.gameserver.templates.skill.EffectTemplate;
import org.l2j.gameserver.utils.ItemFunctions;

import java.util.Collection;

/**
 * @author Bonux
**/
public class AutoHpCpMp extends ScriptVoiceCommandHandler
{
	private static class ChangeCurrentCpListener implements OnChangeCurrentCpListener
	{
		public void onChangeCurrentCp(Creature actor, double oldCp, double newCp)
		{
			if(!actor.isPlayer() || actor.isDead())
				return;

			Player player = actor.getPlayer();

			int percent = player.getVarInt("autocp", 0);
			int currentPercent = (int) (newCp / (player.getMaxCp() / 100.));
			if(percent <= 0 || currentPercent <= 0 || currentPercent > percent)
				return;

			ItemInstance effectedItem = null;
			int effectedItemPower = 0;

			ItemInstance instantItem = null;
			int instantItemPower = 0;

			final Collection<Abnormal> abnormals = player.getAbnormalList().values();
			loop: for(ItemInstance item : player.getInventory().getItems())
			{
				SkillEntry skillEntry = item.getTemplate().getFirstSkill();
				if(skillEntry == null)
					continue;

				Skill skill = skillEntry.getTemplate();
				for(EffectTemplate et : skill.getEffectTemplates(EffectUseType.NORMAL))
				{
					if(et.getEffectType() == EffectType.RestoreCP)
					{
						for(Abnormal abnormal : abnormals)
						{
							if(abnormal.getSkill() == skill)
							{
								for(Effect effect : abnormal.getEffects())
								{
									if(effect.getEffectType() == EffectType.RestoreCP)
									{
										// Не хиляем, если уже наложена какая-либо хилка.
										effectedItem = null;
										effectedItemPower = 0;
										break loop;
									}
								}
							}
						}

						if(!ItemFunctions.checkForceUseItem(player, item, false) || !ItemFunctions.checkUseItem(player, item, false))
							continue loop;

						int power = (int) et.getValue();
						if(power > effectedItemPower)
						{
							if(skill.checkCondition(player, player, false, false, true, false, false))
							{
								effectedItem = item;
								effectedItemPower = power;
								continue loop;
							}
						}
					}
				}
			}

			loop: for(ItemInstance item : player.getInventory().getItems())
			{
				SkillEntry skillEntry = item.getTemplate().getFirstSkill();
				if(skillEntry == null)
					continue;

				if(!ItemFunctions.checkForceUseItem(player, item, false) || !ItemFunctions.checkUseItem(player, item, false))
					continue;

				Skill skill = skillEntry.getTemplate();
				for(EffectTemplate et : skill.getEffectTemplates(EffectUseType.NORMAL_INSTANT))
				{
					if(et.getEffectType() == EffectType.RestoreCP)
					{
						int power = (int) et.getValue();
						if(et.getParam().getBool("percent", false))
							power = power * (int) (player.getMaxCp() / 100.);
						if(power > instantItemPower)
						{
							if(skill.checkCondition(player, player, false, false, true, false, false))
							{
								instantItem = item;
								instantItemPower = power;
								continue loop;
							}
						}
					}
				}
			}

			if(instantItem != null)
				ItemFunctions.useItem(player, instantItem, false, false);

			if(effectedItem != null)
			{
				if(instantItemPower == 0 || percent >= (newCp + instantItemPower) / (player.getMaxCp() / 100.))
					ItemFunctions.useItem(player, effectedItem, false, false);
			}
		}
	}

	private static class ChangeCurrentHpListener implements OnChangeCurrentHpListener
	{
		public void onChangeCurrentHp(Creature actor, double oldHp, double newHp)
		{
			if(!actor.isPlayer() || actor.isDead())
				return;

			Player player = actor.getPlayer();

			int percent = player.getVarInt("autohp", 0);
			int currentPercent = (int) (newHp / (player.getMaxHp() / 100.));
			if(percent <= 0 || currentPercent <= 0 || currentPercent > percent)
				return;

			ItemInstance effectedItem = null;
			int effectedItemPower = 0;

			ItemInstance instantItem = null;
			int instantItemPower = 0;

			final Collection<Abnormal> abnormals = player.getAbnormalList().values();
			loop: for(ItemInstance item : player.getInventory().getItems())
			{
				SkillEntry skillEntry = item.getTemplate().getFirstSkill();
				if(skillEntry == null)
					continue;

				Skill skill = skillEntry.getTemplate();
				for(EffectTemplate et : skill.getEffectTemplates(EffectUseType.NORMAL))
				{
					if(et.getEffectType() == EffectType.RestoreHP)
					{
						for(Abnormal abnormal : abnormals)
						{
							if(abnormal.getSkill() == skill)
							{
								for(Effect effect : abnormal.getEffects())
								{
									if(effect.getEffectType() == EffectType.RestoreHP)
									{
										// Не хиляем, если уже наложена какая-либо хилка.
										effectedItem = null;
										effectedItemPower = 0;
										break loop;
									}
								}
							}
						}

						if(!ItemFunctions.checkForceUseItem(player, item, false) || !ItemFunctions.checkUseItem(player, item, false))
							continue loop;

						int power = (int) et.getValue();
						if(power > effectedItemPower)
						{
							if(skill.checkCondition(player, player, false, false, true, false, false))
							{
								effectedItem = item;
								effectedItemPower = power;
								continue loop;
							}
						}
					}
				}
			}

			loop: for(ItemInstance item : player.getInventory().getItems())
			{
				SkillEntry skillEntry = item.getTemplate().getFirstSkill();
				if(skillEntry == null)
					continue;

				if(!ItemFunctions.checkForceUseItem(player, item, false) || !ItemFunctions.checkUseItem(player, item, false))
					continue;

				Skill skill = skillEntry.getTemplate();
				for(EffectTemplate et : skill.getEffectTemplates(EffectUseType.NORMAL_INSTANT))
				{
					if(et.getEffectType() == EffectType.RestoreHP)
					{
						int power = (int) et.getValue();
						if(et.getParam().getBool("percent", false))
							power = power * (int) (player.getMaxHp() / 100.);
						if(power > instantItemPower)
						{
							if(skill.checkCondition(player, player, false, false, true, false, false))
							{
								instantItem = item;
								instantItemPower = power;
								continue loop;
							}
						}
					}
				}
			}

			if(instantItem != null)
				ItemFunctions.useItem(player, instantItem, false, false);

			if(effectedItem != null)
			{
				if(instantItemPower == 0 || percent >= (newHp + instantItemPower) / (player.getMaxHp() / 100.))
					ItemFunctions.useItem(player, effectedItem, false, false);
			}
		}
	}

	private static class ChangeCurrentMpListener implements OnChangeCurrentMpListener
	{
		public void onChangeCurrentMp(Creature actor, double oldMp, double newMp)
		{
			if(!actor.isPlayer() || actor.isDead())
				return;

			Player player = actor.getPlayer();

			int percent = player.getVarInt("automp", 0);
			int currentPercent = (int) (newMp / (player.getMaxMp() / 100.));
			if(percent <= 0 || currentPercent <= 0 || currentPercent > percent)
				return;

			ItemInstance effectedItem = null;
			int effectedItemPower = 0;

			ItemInstance instantItem = null;
			int instantItemPower = 0;

			final Collection<Abnormal> abnormals = player.getAbnormalList().values();
			loop: for(ItemInstance item : player.getInventory().getItems())
			{
				SkillEntry skillEntry = item.getTemplate().getFirstSkill();
				if(skillEntry == null)
					continue;

				Skill skill = skillEntry.getTemplate();
				for(EffectTemplate et : skill.getEffectTemplates(EffectUseType.NORMAL))
				{
					if(et.getEffectType() == EffectType.RestoreMP)
					{
						for(Abnormal abnormal : abnormals)
						{
							if(abnormal.getSkill() == skill)
							{
								for(Effect effect : abnormal.getEffects())
								{
									if(effect.getEffectType() == EffectType.RestoreMP)
									{
										// Не хиляем, если уже наложена какая-либо хилка.
										effectedItem = null;
										effectedItemPower = 0;
										break loop;
									}
								}
							}
						}

						if(!ItemFunctions.checkForceUseItem(player, item, false) || !ItemFunctions.checkUseItem(player, item, false))
							continue loop;

						int power = (int) et.getValue();
						if(power > effectedItemPower)
						{
							if(skill.checkCondition(player, player, false, false, true, false, false))
							{
								effectedItem = item;
								effectedItemPower = power;
								continue loop;
							}
						}
					}
				}
			}

			loop: for(ItemInstance item : player.getInventory().getItems())
			{
				SkillEntry skillEntry = item.getTemplate().getFirstSkill();
				if(skillEntry == null)
					continue;

				if(!ItemFunctions.checkForceUseItem(player, item, false) || !ItemFunctions.checkUseItem(player, item, false))
					continue;

				Skill skill = skillEntry.getTemplate();
				for(EffectTemplate et : skill.getEffectTemplates(EffectUseType.NORMAL_INSTANT))
				{
					if(et.getEffectType() == EffectType.RestoreMP)
					{
						int power = (int) et.getValue();
						if(et.getParam().getBool("percent", false))
							power = power * (int) (player.getMaxMp() / 100.);
						if(power > instantItemPower)
						{
							if(skill.checkCondition(player, player, false, false, true, false, false))
							{
								instantItem = item;
								instantItemPower = power;
								continue loop;
							}
						}
					}
				}
			}

			if(instantItem != null)
				ItemFunctions.useItem(player, instantItem, false, false);

			if(effectedItem != null)
			{
				if(instantItemPower == 0 || percent >= (newMp + instantItemPower) / (player.getMaxMp() / 100.))
					ItemFunctions.useItem(player, effectedItem, false, false);
			}
		}
	}

	private static class PlayerEnterListener implements OnPlayerEnterListener
	{
		public void onPlayerEnter(Player player)
		{
			if(!Config.ALLOW_AUTOHEAL_COMMANDS)
				return;

			int percent = player.getVarInt("autocp", 0);
			if(percent > 0)
			{
				player.addListener(CHANGE_CURRENT_CP_LISTENER);
				player.sendMessage("You are using an automatic CP recovery. Your CP will automatically recover at a value of " + percent + "% or less.");
			}
			percent = player.getVarInt("autohp", 0);
			if(percent > 0)
			{
				player.addListener(CHANGE_CURRENT_HP_LISTENER);
				player.sendMessage("You are using an automatic HP recovery. Your HP will automatically recover at a value of " + percent + "% or less.");
			}
			percent = player.getVarInt("automp", 0);
			if(percent > 0)
			{
				player.addListener(CHANGE_CURRENT_MP_LISTENER);
				player.sendMessage("You are using an automatic MP recovery. Your MP will automatically recover at a value of " + percent + "% or less.");
			}
		}
	}

	private static final OnChangeCurrentCpListener CHANGE_CURRENT_CP_LISTENER = new ChangeCurrentCpListener();
	private static final OnChangeCurrentHpListener CHANGE_CURRENT_HP_LISTENER = new ChangeCurrentHpListener();
	private static final OnChangeCurrentMpListener CHANGE_CURRENT_MP_LISTENER = new ChangeCurrentMpListener();
	private static final OnPlayerEnterListener PLAYER_ENTER_LISTENER = new PlayerEnterListener();

	private static final String[] COMMANDS = new String[] { "autocp", "autohp", "automp" };

	@Override
	public boolean useVoicedCommand(String command, Player activeChar, String args)
	{
		if(!Config.ALLOW_AUTOHEAL_COMMANDS)
			return false;

		if(command.equalsIgnoreCase("autocp"))
		{
			if(activeChar.getVarInt("autocp", 0) > 0)
			{
				activeChar.removeListener(CHANGE_CURRENT_CP_LISTENER);
				activeChar.unsetVar("autocp");
				activeChar.sendMessage("CP automatic recovery system disabled.");
			}
			else
			{
				int percent;
				try
				{
					percent = Math.min(99, Integer.parseInt(args));
				}
				catch(NumberFormatException e)
				{
					activeChar.sendMessage("Incorrect use commands! Use: .autocp [CP_PERCENT_FOR EARLY_RECOVERY]");
					return false;
				}
				if(percent <= 0)
				{
					activeChar.sendMessage("You can not specify zero or negative value!");
					return false;
				}
				activeChar.addListener(CHANGE_CURRENT_CP_LISTENER);
				activeChar.setVar("autocp", percent, -1);
				activeChar.sendMessage("You have enabled an automatic CP recovery. Your CP will automatically recover at a value of " + percent + "% or less.");
			}
			return true;
		}
		else if(command.equalsIgnoreCase("autohp"))
		{
			if(activeChar.getVarInt("autohp", 0) > 0)
			{
				activeChar.removeListener(CHANGE_CURRENT_HP_LISTENER);
				activeChar.unsetVar("autohp");
				activeChar.sendMessage("HP automatic recovery system disabled.");
			}
			else
			{
				int percent;
				try
				{
					percent = Math.min(99, Integer.parseInt(args));
				}
				catch(NumberFormatException e)
				{
					activeChar.sendMessage("Incorrect use commands! Use: .autohp [HP_PERCENT_FOR EARLY_RECOVERY]");
					return false;
				}
				if(percent <= 0)
				{
					activeChar.sendMessage("You can not specify zero or negative value!");
					return false;
				}
				activeChar.addListener(CHANGE_CURRENT_HP_LISTENER);
				activeChar.setVar("autohp", percent, -1);
				activeChar.sendMessage("You have enabled an automatic HP recovery. Your HP will automatically recover at a value of " + percent + "% or less.");
			}
			return true;
		}
		else if(command.equalsIgnoreCase("automp"))
		{
			if(activeChar.getVarInt("automp", 0) > 0)
			{
				activeChar.removeListener(CHANGE_CURRENT_MP_LISTENER);
				activeChar.unsetVar("automp");
				activeChar.sendMessage("MP automatic recovery system disabled.");
			}
			else
			{
				int percent;
				try
				{
					percent = Math.min(99, Integer.parseInt(args));
				}
				catch(NumberFormatException e)
				{
					activeChar.sendMessage("Incorrect use commands! Use: .automp [MP_PERCENT_FOR EARLY_RECOVERY]");
					return false;
				}
				if(percent <= 0)
				{
					activeChar.sendMessage("You can not specify zero or negative value!");
					return false;
				}
				activeChar.addListener(CHANGE_CURRENT_MP_LISTENER);
				activeChar.setVar("automp", percent, -1);
				activeChar.sendMessage("You have enabled an automatic MP recovery. Your MP will automatically recover at a value of " + percent + "% or less.");
			}
			return true;
		}
		return false;
	}

	@Override
	public void onInit()
	{
		super.onInit();
		CharListenerList.addGlobal(PLAYER_ENTER_LISTENER);
	}

	@Override
	public String[] getVoicedCommandList()
	{
		return COMMANDS;
	}
}