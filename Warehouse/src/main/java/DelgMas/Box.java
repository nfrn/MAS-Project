package DelgMas;

import com.github.rinde.rinsim.core.model.pdp.Parcel;
import com.github.rinde.rinsim.core.model.pdp.ParcelDTO;

public class Box extends Parcel {
    boolean finaldestination;

    public Box(ParcelDTO parcelDto, boolean finaldestination) {

        super(parcelDto);
        this.finaldestination = finaldestination;
    }
}