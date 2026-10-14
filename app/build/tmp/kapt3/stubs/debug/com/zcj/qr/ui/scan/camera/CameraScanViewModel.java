package com.zcj.qr.ui.scan.camera;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000&\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0010\u000e\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0002\b\u0002\u0018\u00002\u00020\u0001B\u0005\u00a2\u0006\u0002\u0010\u0002J\u000e\u0010\n\u001a\u00020\u000b2\u0006\u0010\f\u001a\u00020\u0007R\u000e\u0010\u0003\u001a\u00020\u0004X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0019\u0010\u0005\u001a\n\u0012\u0006\u0012\u0004\u0018\u00010\u00070\u0006\u00a2\u0006\b\n\u0000\u001a\u0004\b\b\u0010\t\u00a8\u0006\r"}, d2 = {"Lcom/zcj/qr/ui/scan/camera/CameraScanViewModel;", "Landroidx/lifecycle/ViewModel;", "()V", "repository", "Lcom/zcj/qr/data/repository/HistoryRepository;", "scannedResult", "Landroidx/lifecycle/MutableLiveData;", "", "getScannedResult", "()Landroidx/lifecycle/MutableLiveData;", "onScanResult", "", "content", "app_debug"})
public final class CameraScanViewModel extends androidx.lifecycle.ViewModel {
    @org.jetbrains.annotations.NotNull()
    private final com.zcj.qr.data.repository.HistoryRepository repository = null;
    @org.jetbrains.annotations.NotNull()
    private final androidx.lifecycle.MutableLiveData<java.lang.String> scannedResult = null;
    
    public CameraScanViewModel() {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public final androidx.lifecycle.MutableLiveData<java.lang.String> getScannedResult() {
        return null;
    }
    
    public final void onScanResult(@org.jetbrains.annotations.NotNull()
    java.lang.String content) {
    }
}