package com.zcj.qr.data.dao;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000@\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0003\n\u0002\u0010\t\n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\b\n\u0002\b\u000b\bg\u0018\u00002\u00020\u0001J\u000e\u0010\u0002\u001a\u00020\u0003H\u00a7@\u00a2\u0006\u0002\u0010\u0004J\u0016\u0010\u0005\u001a\u00020\u00032\u0006\u0010\u0006\u001a\u00020\u0007H\u00a7@\u00a2\u0006\u0002\u0010\bJ\u0016\u0010\t\u001a\u00020\n2\u0006\u0010\u000b\u001a\u00020\fH\u00a7@\u00a2\u0006\u0002\u0010\rJ\u0014\u0010\u000e\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00110\u00100\u000fH\'J\u0018\u0010\u0012\u001a\u0004\u0018\u00010\u00072\u0006\u0010\u000b\u001a\u00020\fH\u00a7@\u00a2\u0006\u0002\u0010\rJ\u001c\u0010\u0013\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00110\u00100\u000f2\u0006\u0010\u0014\u001a\u00020\u0015H\'J\u0016\u0010\u0016\u001a\u00020\u00072\u0006\u0010\u0017\u001a\u00020\u0011H\u00a7@\u00a2\u0006\u0002\u0010\u0018JB\u0010\u0019\u001a\u00020\u00032\u0006\u0010\u0006\u001a\u00020\u00072\u0006\u0010\u001a\u001a\u00020\f2\u0006\u0010\u001b\u001a\u00020\u00072\u0006\u0010\u001c\u001a\u00020\n2\b\u0010\u001d\u001a\u0004\u0018\u00010\f2\b\u0010\u001e\u001a\u0004\u0018\u00010\fH\u00a7@\u00a2\u0006\u0002\u0010\u001f\u00a8\u0006 "}, d2 = {"Lcom/zcj/qr/data/dao/GenerateHistoryDao;", "", "clearAll", "", "(Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "delete", "id", "", "(JLkotlin/coroutines/Continuation;)Ljava/lang/Object;", "exists", "", "content", "", "(Ljava/lang/String;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "getAll", "Lkotlinx/coroutines/flow/Flow;", "", "Lcom/zcj/qr/data/model/GenerateHistoryEntity;", "getIdForContent", "getLatest", "limit", "", "insert", "entity", "(Lcom/zcj/qr/data/model/GenerateHistoryEntity;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "update", "type", "timestamp", "hasEncryption", "styleJson", "logoFilePath", "(JLjava/lang/String;JZLjava/lang/String;Ljava/lang/String;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "app_debug"})
@androidx.room.Dao()
public abstract interface GenerateHistoryDao {
    
    @androidx.room.Query(value = "SELECT * FROM generate_history ORDER BY timestamp DESC")
    @org.jetbrains.annotations.NotNull()
    public abstract kotlinx.coroutines.flow.Flow<java.util.List<com.zcj.qr.data.model.GenerateHistoryEntity>> getAll();
    
    @androidx.room.Query(value = "SELECT * FROM generate_history ORDER BY timestamp DESC LIMIT :limit")
    @org.jetbrains.annotations.NotNull()
    public abstract kotlinx.coroutines.flow.Flow<java.util.List<com.zcj.qr.data.model.GenerateHistoryEntity>> getLatest(int limit);
    
    @androidx.room.Query(value = "SELECT COUNT(*) > 0 FROM generate_history WHERE content = :content")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object exists(@org.jetbrains.annotations.NotNull()
    java.lang.String content, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.lang.Boolean> $completion);
    
    @androidx.room.Query(value = "SELECT id FROM generate_history WHERE content = :content LIMIT 1")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object getIdForContent(@org.jetbrains.annotations.NotNull()
    java.lang.String content, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.lang.Long> $completion);
    
    @androidx.room.Query(value = "UPDATE generate_history SET type = :type, timestamp = :timestamp, hasEncryption = :hasEncryption, styleJson = :styleJson, logoFilePath = :logoFilePath WHERE id = :id")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object update(long id, @org.jetbrains.annotations.NotNull()
    java.lang.String type, long timestamp, boolean hasEncryption, @org.jetbrains.annotations.Nullable()
    java.lang.String styleJson, @org.jetbrains.annotations.Nullable()
    java.lang.String logoFilePath, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
    
    @androidx.room.Insert(onConflict = 1)
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object insert(@org.jetbrains.annotations.NotNull()
    com.zcj.qr.data.model.GenerateHistoryEntity entity, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.lang.Long> $completion);
    
    @androidx.room.Query(value = "DELETE FROM generate_history WHERE id = :id")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object delete(long id, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
    
    @androidx.room.Query(value = "DELETE FROM generate_history")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object clearAll(@org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
}