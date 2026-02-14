package net.cdahmedeh.poetwrite.ui.view;

import com.formdev.flatlaf.extras.FlatSVGIcon;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import net.cdahmedeh.poetwrite.ui.controller.MenuViewController;
import net.cdahmedeh.poetwrite.ui.model.MenuViewModel;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class MenuView extends View<MenuViewModel, MenuViewController, JMenuBar> {
    private JMenuItem generateRandomText;
    private JMenuBar mb;


    public MenuView(MenuViewModel viewModel, MenuViewController viewController) {
        super(viewModel, viewController);
    }

    @Override
    protected void setup() {
        mb = new JMenuBar();
        JMenu help;

        JMenu file = new JMenu("File");
        JMenuItem exit = new JMenuItem("Exit");
        file.add(exit);

        help = new JMenu("Tools");
        generateRandomText = new JMenuItem("Generate Random Text");
        help.add(generateRandomText);

        FlatSVGIcon anotherIcon = new FlatSVGIcon(getClass().getResource("/icons/generate.svg"));
        generateRandomText.setIcon(anotherIcon);

        mb.add(file);
        mb.add(help);

        FlatSVGIcon defaultIcon = new FlatSVGIcon(getClass().getResource("/icons/exit.svg"));
        exit.setIcon(defaultIcon);

        generateRandomText.addActionListener(e -> viewController.generateRandomText());
        exit.addActionListener(e -> viewController.closeApp());
    }

    @Override
    public JMenuBar root() {
        return mb;
    }

    @Override
    protected void subscribe(CompositeDisposable disposable) {

    }

    BufferedImage scaleIcon(ImageIcon src, int w, int h) {
        BufferedImage img = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = img.createGraphics();

        g2.setRenderingHint(
                RenderingHints.KEY_INTERPOLATION,
                RenderingHints.VALUE_INTERPOLATION_BILINEAR
        );
        g2.drawImage(src.getImage(), 0, 0, w, h, null);
        g2.dispose();

        return img;
    }
}
