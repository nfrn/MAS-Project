package DelgMas;

import com.github.rinde.rinsim.core.model.DependencyProvider;
import com.github.rinde.rinsim.core.model.ModelBuilder;
import com.github.rinde.rinsim.core.model.pdp.PDPModel;
import com.github.rinde.rinsim.core.model.pdp.Parcel;
import com.github.rinde.rinsim.core.model.pdp.Vehicle;
import com.github.rinde.rinsim.core.model.road.RoadModel;
import com.github.rinde.rinsim.geom.Point;
import com.github.rinde.rinsim.ui.renderers.CanvasRenderer;
import com.github.rinde.rinsim.ui.renderers.UiSchema;
import com.github.rinde.rinsim.ui.renderers.ViewPort;
import com.google.auto.value.AutoValue;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;

import java.util.Collection;
import java.util.Iterator;

import static DelgMas.AgvExample.NUM_AGVS;
import static com.google.common.base.Preconditions.checkState;

public class BoxRender extends CanvasRenderer.AbstractCanvasRenderer {

    static final Point LABEL_OFFSET = new Point(-5, -20);
    final UiSchema uiSchema;
    RoadModel roadModel;
    AgvModel agvModel;

    BoxRender(RoadModel rm, AgvModel am) {

        roadModel = rm;
        agvModel = am;
        uiSchema = new UiSchema(false);
        uiSchema.add(Box.class, "/graphics/perspective/deliverypackage2.png");
    }

    static Builder builder() {
        return new AutoValue_BoxRender_Builder();
    }

    @Override
    public void renderStatic(GC gc, ViewPort viewPort) {

    }

    @Override
    public synchronized void renderDynamic(GC gc, ViewPort viewPort, long l) {
        uiSchema.initialize(gc.getDevice());
            final Collection<Parcel> parcels = agvModel.getParcels(PDPModel.ParcelState.AVAILABLE);
            final Image image = uiSchema.getImage(Box.class);
            checkState(image != null);

            for (Iterator<Parcel> it = parcels.iterator(); it.hasNext(); ) {
                Parcel p = it.next();
                if (p instanceof Box) {
                    Box box = (Box) p;
//                if (storageTime != null) {
//                  power = battery.capacity;
//                }
//                final String text = String.format("%.0f ", (double) power / agent.POWERLIMIT * 100) + "%";
//                if (roadModel.getObjectsOfType(Box.class).size() != Nu) {
//                    break;
//                }
                    try {
                        final Point pos = roadModel.getPosition(p);
                        final int x = viewPort.toCoordX(pos.x);
                        final int y = viewPort.toCoordY(pos.y);
                        if (box.isAvailable) {
                            gc.setBackground(gc.getDevice().getSystemColor(SWT.COLOR_GREEN));
                            gc.fillOval(x, y, 15,  15);
                        } else {
                            gc.setBackground(gc.getDevice().getSystemColor(SWT.COLOR_RED));
                            gc.fillOval(x, y, 15, 15);
                            final String text = String.format("%d ", box.getStorageTime());
                            final int textWidth = gc.textExtent(text).x;
                            gc.drawText(text, (int) x + 20,
                                    (int) y, true);
                        }
                    } catch (Exception e) {
                        continue;
                    }
                }
                    /*
                    final int textWidth = gc.textExtent(text).x;
                    gc.setBackground(gc.getDevice().getSystemColor(SWT.COLOR_BLUE));
                    gc.drawText(text, (int) LABEL_OFFSET.x + x - textWidth / 2,
                            (int) LABEL_OFFSET.y + y, true);*/


        }
    }


    @AutoValue
    abstract static class Builder
            extends ModelBuilder.AbstractModelBuilder<BoxRender, Void> {
        private static final long serialVersionUID = 3349625514419113101L;

        Builder() {
            setDependencies(RoadModel.class, AgvModel.class);
        }

        @Override
        public BoxRender build(DependencyProvider dependencyProvider) {
            final RoadModel rm = dependencyProvider.get(RoadModel.class);
            final AgvModel pm = dependencyProvider.get(AgvModel.class);
            return new BoxRender(rm, pm);
        }
    }
}
