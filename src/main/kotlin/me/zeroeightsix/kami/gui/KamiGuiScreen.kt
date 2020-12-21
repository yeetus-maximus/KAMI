package me.zeroeightsix.kami.gui

import glm_.vec4.Vec4
import imgui.Col
import imgui.ImGui
import me.zeroeightsix.kami.feature.hidden.PrepHandler
import me.zeroeightsix.kami.gui.widgets.EnabledWidgets
import me.zeroeightsix.kami.gui.windows.Settings
import me.zeroeightsix.kami.gui.windows.modules.Modules
import me.zeroeightsix.kami.gui.wizard.Wizard
import me.zeroeightsix.kami.mc
import me.zeroeightsix.kami.util.text
import net.minecraft.client.gui.screen.Screen
import net.minecraft.client.util.math.MatrixStack

object KamiGuiScreen : ImGuiScreen(text(null, "Kami GUI")) {

    var parent: Screen? = null
    private val colourIndices = Col.values().map { it.i }

    override fun render(matrices: MatrixStack?, mouseX: Int, mouseY: Int, delta: Float) {
        if (parent != null) {
            // If we have a screen to return to, draw the background.
            // Usually parent will be nonnull if the GUI was opened from e.g. the title menu, so it would have no
            // background if this render doesn't happen.
            this.renderBackground(matrices)
        }
        super.render(matrices, mouseX, mouseY, delta)

        KamiHud.frame(matrices!!) {
            if (Wizard()) return@frame

            this()
        }
    }

    operator fun invoke() {
        if (Settings.rainbowMode) {
            Themes.Variants.values()[Settings.styleIdx].applyStyle(false)
            val colors = ImGui.style.colors
            colourIndices.forEach { idx ->
                val col = colors[idx]
                val buf = FloatArray(3)
                ImGui.colorConvertRGBtoHSV(col.toVec3().toFloatArray(), buf)
                buf[0] = PrepHandler.getRainbowHue(buf[0].toDouble()).toFloat()
                ImGui.colorConvertHSVtoRGB(buf, buf)
                colors[idx] = Vec4(buf[0], buf[1], buf[2], col[3])
            }
        }

        // Draw the main menu bar.
        MenuBar()
        // Debug window (theme, demo window)
        if (View.demoWindowVisible) {
            ImGui.showDemoWindow(View::demoWindowVisible)
        }
        // Draw all module windows
        Modules()
        // Draw the settings
        Settings()

        if (!EnabledWidgets.hideAll) {
            showWidgets()
        }
    }

    override fun onClose() {
        mc.openScreen(this.parent)
    }

    fun showWidgets(limitY: Boolean = true) {
        EnabledWidgets.widgets.removeAll {
            it.open && it.showWindow(limitY)
        }
    }
}
