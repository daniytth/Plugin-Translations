package com.artillexstudios.axcrates.commands.utils;

import com.artillexstudios.axapi.utils.StringUtils;
import org.jetbrains.annotations.NotNull;
import revxrsal.commands.bukkit.actor.BukkitCommandActor;
import revxrsal.commands.bukkit.exception.BukkitExceptionHandler;
import revxrsal.commands.bukkit.exception.EmptyEntitySelectorException;
import revxrsal.commands.bukkit.exception.InvalidPlayerException;
import revxrsal.commands.bukkit.exception.InvalidWorldException;
import revxrsal.commands.bukkit.exception.MalformedEntitySelectorException;
import revxrsal.commands.bukkit.exception.MissingLocationParameterException;
import revxrsal.commands.bukkit.exception.MoreThanOneEntityException;
import revxrsal.commands.bukkit.exception.NonPlayerEntitiesException;
import revxrsal.commands.bukkit.exception.SenderNotConsoleException;
import revxrsal.commands.bukkit.exception.SenderNotPlayerException;
import revxrsal.commands.bukkit.util.BukkitUtils;
import revxrsal.commands.exception.EnumNotFoundException;
import revxrsal.commands.exception.ExpectedLiteralException;
import revxrsal.commands.exception.InputParseException;
import revxrsal.commands.exception.InvalidBooleanException;
import revxrsal.commands.exception.InvalidDecimalException;
import revxrsal.commands.exception.InvalidHelpPageException;
import revxrsal.commands.exception.InvalidIntegerException;
import revxrsal.commands.exception.InvalidListSizeException;
import revxrsal.commands.exception.InvalidStringSizeException;
import revxrsal.commands.exception.InvalidUUIDException;
import revxrsal.commands.exception.MissingArgumentException;
import revxrsal.commands.exception.NoPermissionException;
import revxrsal.commands.exception.NumberNotInRangeException;
import revxrsal.commands.exception.UnknownCommandException;
import revxrsal.commands.node.ParameterNode;

import static com.artillexstudios.axcrates.AxCrates.LANG;

public class CommandExceptions extends BukkitExceptionHandler {

    @HandleException
    public void onInvalidPlayer(InvalidPlayerException e, BukkitCommandActor actor) {
        actor.error(StringUtils.formatToString(
                LANG.getString("commands.invalid-player")
                        .replace("%player%", e.input())
        ));
    }

    @HandleException
    public void onInvalidWorld(InvalidWorldException e, BukkitCommandActor actor) {
        actor.error(StringUtils.formatToString(
                LANG.getString("commands.invalid-value")
                        .replace("%value%", e.input())
        ));
    }

    @HandleException
    public void onInvalidWorld(MissingLocationParameterException e, BukkitCommandActor actor) {
        actor.error(StringUtils.formatToString(
                LANG.getString("commands.invalid-value")
                        .replace("%value%", e.input())
        ));
    }

    @HandleException
    public void onSenderNotConsole(SenderNotConsoleException e, BukkitCommandActor actor) {
        actor.error(StringUtils.formatToString(
                LANG.getString("commands.console-only")
        ));
    }

    @HandleException
    public void onSenderNotPlayer(SenderNotPlayerException e, BukkitCommandActor actor) {
        actor.error(StringUtils.formatToString(
                LANG.getString("commands.player-only")
        ));
    }

    @HandleException
    public void onMalformedEntitySelector(MalformedEntitySelectorException e, BukkitCommandActor actor) {
        actor.error(StringUtils.formatToString(
                LANG.getString("commands.invalid-selector")
        ));
    }

    @HandleException
    public void onNonPlayerEntities(NonPlayerEntitiesException e, BukkitCommandActor actor) {
        actor.error(StringUtils.formatToString(
                LANG.getString("commands.invalid-selector")
        ));
    }

    @HandleException
    public void onMoreThanOneEntity(MoreThanOneEntityException e, BukkitCommandActor actor) {
        actor.error(StringUtils.formatToString(
                LANG.getString("commands.invalid-selector")
        ));
    }

    @HandleException
    public void onEmptyEntitySelector(EmptyEntitySelectorException e, BukkitCommandActor actor) {
        actor.error(StringUtils.formatToString(
                LANG.getString("commands.invalid-selector")
        ));
    }

    public void onEnumNotFound(@NotNull EnumNotFoundException e, @NotNull BukkitCommandActor actor) {
        actor.error(StringUtils.formatToString(
                LANG.getString("commands.invalid-value")
                        .replace("%value%", e.input())
        ));
    }

    public void onExpectedLiteral(@NotNull ExpectedLiteralException e, @NotNull BukkitCommandActor actor) {
        actor.error(BukkitUtils.legacyColorize("&cVárt: &e" + e.node().name() + "&c, de ezt kapta: &e" + e.input() + "&c."));
    }

    public void onInputParse(@NotNull InputParseException e, @NotNull BukkitCommandActor actor) {
        switch (e.cause()) {
            case INVALID_ESCAPE_CHARACTER:
                actor.error(BukkitUtils.legacyColorize("&cÉrvénytelen Bemenet. Használd &e\\\\ &cBlackslash importáj."));
                break;
            case UNCLOSED_QUOTE:
                actor.error(BukkitUtils.legacyColorize("&cNyitott idézőjel. Győződjön meg róla, hogy minden idézőjelet bezár."));
                break;
            case EXPECTED_WHITESPACE:
                actor.error(BukkitUtils.legacyColorize("&cEgy argumentum végére szóközre számítottam, de további adatokat találtam."));
        }

    }

    public void onInvalidListSize(@NotNull InvalidListSizeException e, @NotNull BukkitCommandActor actor, @NotNull ParameterNode<BukkitCommandActor, ?> parameter) {
        if (e.inputSize() < e.minimum()) {
            actor.error(BukkitUtils.legacyColorize("&cLegalább &e" + fmt(e.minimum()) + " &cdarabot kell megadnod ehhez: &e" + parameter.name() + "&c."));
        }

        if (e.inputSize() > e.maximum()) {
            actor.error(BukkitUtils.legacyColorize("&cLegfeljebb &e" + fmt(e.maximum()) + " &cdarabot adhatsz meg ehhez: &e" + parameter.name() + "&c."));
        }

    }

    public void onInvalidStringSize(@NotNull InvalidStringSizeException e, @NotNull BukkitCommandActor actor, @NotNull ParameterNode<BukkitCommandActor, ?> parameter) {
        if (e.input().length() < e.minimum()) {
            actor.error(BukkitUtils.legacyColorize("&cA(z) &e" + parameter.name() + " &cparaméternek legalább &e" + fmt(e.minimum()) + " &ckarakter hosszúnak kell lennie."));
        }

        if (e.input().length() > e.maximum()) {
            actor.error(BukkitUtils.legacyColorize("&cA(z) &e" + parameter.name() + " &cparaméter legfeljebb &e" + fmt(e.maximum()) + " &ckarakter hosszú lehet."));
        }

    }

    public void onInvalidBoolean(@NotNull InvalidBooleanException e, @NotNull BukkitCommandActor actor) {
        actor.error(StringUtils.formatToString(
                LANG.getString("commands.invalid-value")
                        .replace("%value%", e.input())
        ));
    }

    public void onInvalidDecimal(@NotNull InvalidDecimalException e, @NotNull BukkitCommandActor actor) {
        actor.error(StringUtils.formatToString(
                LANG.getString("commands.invalid-value")
                        .replace("%value%", e.input())
        ));
    }

    public void onInvalidInteger(@NotNull InvalidIntegerException e, @NotNull BukkitCommandActor actor) {
        actor.error(StringUtils.formatToString(
                LANG.getString("commands.invalid-value")
                        .replace("%value%", e.input())
        ));
    }

    public void onInvalidUUID(@NotNull InvalidUUIDException e, @NotNull BukkitCommandActor actor) {
        actor.error(StringUtils.formatToString(
                LANG.getString("commands.invalid-value")
                        .replace("%value%", e.input())
        ));
    }

    public void onMissingArgument(@NotNull MissingArgumentException e, @NotNull BukkitCommandActor actor, @NotNull ParameterNode<BukkitCommandActor, ?> parameter) {
        actor.error(BukkitUtils.legacyColorize("&cHiányzik egy kötelező paraméter: &e" + parameter.name() + "&c. Használat: &e/" + parameter.command().usage() + "&c."));
    }

    public void onNoPermission(@NotNull NoPermissionException e, @NotNull BukkitCommandActor actor) {
        actor.error(StringUtils.formatToString(
                LANG.getString("commands.no-permission")
        ));
    }

    public void onNumberNotInRange(@NotNull NumberNotInRangeException e, @NotNull BukkitCommandActor actor, @NotNull ParameterNode<BukkitCommandActor, Number> parameter) {
        actor.error(StringUtils.formatToString(
                LANG.getString("commands.out-of-range")
                        .replace("%number%", fmt(e.input()))
                        .replace("%min%", fmt(e.minimum()))
                        .replace("%max%", fmt(e.maximum()))
        ));
    }

    public void onInvalidHelpPage(@NotNull InvalidHelpPageException e, @NotNull BukkitCommandActor actor) {
        if (e.numberOfPages() == 1) {
            actor.error(BukkitUtils.legacyColorize("Érvénytelen súgó oldal: &e" + e.page() + "&c. Csak 1 lehet."));
        } else {
            actor.error(BukkitUtils.legacyColorize("Érvénytelen súgó oldal: &e" + e.page() + "&c. &e1 &cés &e"  + e.numberOfPages()));
        }
    }

    public void onUnknownCommand(@NotNull UnknownCommandException e, @NotNull BukkitCommandActor actor) {
        actor.error(StringUtils.formatToString(
                LANG.getString("commands.invalid-command")
        ));
    }
}
