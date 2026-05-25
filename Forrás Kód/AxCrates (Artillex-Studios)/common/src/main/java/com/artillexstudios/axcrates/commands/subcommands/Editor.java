package com.artillexstudios.axcrates.commands.subcommands;

import com.artillexstudios.axcrates.crates.Crate;
import com.artillexstudios.axcrates.editor.impl.CrateEditor;
import com.artillexstudios.axcrates.editor.impl.CrateSettingEditor;
import com.artillexstudios.axcrates.editor.impl.MainEditor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

public enum Editor {
    INSTANCE;

    public void execute(Player sender, @Nullable Crate crate) {
        MainEditor mainEditor = new MainEditor(sender);
        if (crate != null) {
            new CrateSettingEditor(sender, new CrateEditor(sender, mainEditor), crate).open();
            return;
        }
        mainEditor.open();
    }
}
