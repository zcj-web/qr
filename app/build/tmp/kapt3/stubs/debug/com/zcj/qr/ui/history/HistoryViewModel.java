package com.zcj.qr.ui.history;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u00008\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\t\n\u0002\b\u0002\u0018\u00002\u00020\u0001B\u0005\u00a2\u0006\u0002\u0010\u0002J\u000e\u0010\u000e\u001a\u00020\u000f2\u0006\u0010\u0010\u001a\u00020\u0011J\u000e\u0010\u0012\u001a\u00020\u000f2\u0006\u0010\u0010\u001a\u00020\u0011R\u001d\u0010\u0003\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00060\u00050\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0007\u0010\bR\u000e\u0010\t\u001a\u00020\nX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u001d\u0010\u000b\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\f0\u00050\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b\r\u0010\b\u00a8\u0006\u0013"}, d2 = {"Lcom/zcj/qr/ui/history/HistoryViewModel;", "Landroidx/lifecycle/ViewModel;", "()V", "generateHistory", "Lkotlinx/coroutines/flow/Flow;", "", "Lcom/zcj/qr/data/model/GenerateHistoryEntity;", "getGenerateHistory", "()Lkotlinx/coroutines/flow/Flow;", "repository", "Lcom/zcj/qr/data/repository/HistoryRepository;", "scanHistory", "Lcom/zcj/qr/data/model/ScanHistoryEntity;", "getScanHistory", "deleteGenerate", "", "id", "", "deleteScan", "app_debug"})
public final class HistoryViewModel extends androidx.lifecycle.ViewModel {
    @org.jetbrains.annotations.NotNull()
    private final com.zcj.qr.data.repository.HistoryRepository repository = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.Flow<java.util.List<com.zcj.qr.data.model.ScanHistoryEntity>> scanHistory = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.Flow<java.util.List<com.zcj.qr.data.model.GenerateHistoryEntity>> generateHistory = null;
    
    public HistoryViewModel() {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.Flow<java.util.List<com.zcj.qr.data.model.ScanHistoryEntity>> getScanHistory() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.Flow<java.util.List<com.zcj.qr.data.model.GenerateHistoryEntity>> getGenerateHistory() {
        return null;
    }
    
    public final void deleteScan(long id) {
    }
    
    public final void deleteGenerate(long id) {
    }
}