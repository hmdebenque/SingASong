package com.frenchfrie.singasong.reader;
import android.os.ParcelFileDescriptor;
interface IPdfWorker {
    int inspect(in ParcelFileDescriptor source);
    void render(in ParcelFileDescriptor source, in ParcelFileDescriptor destination, int pageIndex, int width);
}
