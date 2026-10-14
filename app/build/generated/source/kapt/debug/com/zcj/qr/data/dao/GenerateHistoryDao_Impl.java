package com.zcj.qr.data.dao;

import android.database.Cursor;
import android.os.CancellationSignal;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.SharedSQLiteStatement;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import com.zcj.qr.data.model.GenerateHistoryEntity;
import java.lang.Boolean;
import java.lang.Class;
import java.lang.Exception;
import java.lang.Integer;
import java.lang.Long;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import javax.annotation.processing.Generated;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlinx.coroutines.flow.Flow;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class GenerateHistoryDao_Impl implements GenerateHistoryDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<GenerateHistoryEntity> __insertionAdapterOfGenerateHistoryEntity;

  private final SharedSQLiteStatement __preparedStmtOfUpdate;

  private final SharedSQLiteStatement __preparedStmtOfDelete;

  private final SharedSQLiteStatement __preparedStmtOfClearAll;

  public GenerateHistoryDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfGenerateHistoryEntity = new EntityInsertionAdapter<GenerateHistoryEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `generate_history` (`id`,`content`,`type`,`timestamp`,`hasEncryption`,`logoFilePath`,`styleJson`) VALUES (nullif(?, 0),?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final GenerateHistoryEntity entity) {
        statement.bindLong(1, entity.getId());
        if (entity.getContent() == null) {
          statement.bindNull(2);
        } else {
          statement.bindString(2, entity.getContent());
        }
        if (entity.getType() == null) {
          statement.bindNull(3);
        } else {
          statement.bindString(3, entity.getType());
        }
        statement.bindLong(4, entity.getTimestamp());
        final int _tmp = entity.getHasEncryption() ? 1 : 0;
        statement.bindLong(5, _tmp);
        if (entity.getLogoFilePath() == null) {
          statement.bindNull(6);
        } else {
          statement.bindString(6, entity.getLogoFilePath());
        }
        if (entity.getStyleJson() == null) {
          statement.bindNull(7);
        } else {
          statement.bindString(7, entity.getStyleJson());
        }
      }
    };
    this.__preparedStmtOfUpdate = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE generate_history SET type = ?, timestamp = ?, hasEncryption = ?, styleJson = ?, logoFilePath = ? WHERE id = ?";
        return _query;
      }
    };
    this.__preparedStmtOfDelete = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM generate_history WHERE id = ?";
        return _query;
      }
    };
    this.__preparedStmtOfClearAll = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM generate_history";
        return _query;
      }
    };
  }

  @Override
  public Object insert(final GenerateHistoryEntity entity,
      final Continuation<? super Long> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Long>() {
      @Override
      @NonNull
      public Long call() throws Exception {
        __db.beginTransaction();
        try {
          final Long _result = __insertionAdapterOfGenerateHistoryEntity.insertAndReturnId(entity);
          __db.setTransactionSuccessful();
          return _result;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object update(final long id, final String type, final long timestamp,
      final boolean hasEncryption, final String styleJson, final String logoFilePath,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfUpdate.acquire();
        int _argIndex = 1;
        if (type == null) {
          _stmt.bindNull(_argIndex);
        } else {
          _stmt.bindString(_argIndex, type);
        }
        _argIndex = 2;
        _stmt.bindLong(_argIndex, timestamp);
        _argIndex = 3;
        final int _tmp = hasEncryption ? 1 : 0;
        _stmt.bindLong(_argIndex, _tmp);
        _argIndex = 4;
        if (styleJson == null) {
          _stmt.bindNull(_argIndex);
        } else {
          _stmt.bindString(_argIndex, styleJson);
        }
        _argIndex = 5;
        if (logoFilePath == null) {
          _stmt.bindNull(_argIndex);
        } else {
          _stmt.bindString(_argIndex, logoFilePath);
        }
        _argIndex = 6;
        _stmt.bindLong(_argIndex, id);
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfUpdate.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object delete(final long id, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDelete.acquire();
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, id);
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfDelete.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object clearAll(final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfClearAll.acquire();
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfClearAll.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<GenerateHistoryEntity>> getAll() {
    final String _sql = "SELECT * FROM generate_history ORDER BY timestamp DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"generate_history"}, new Callable<List<GenerateHistoryEntity>>() {
      @Override
      @NonNull
      public List<GenerateHistoryEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfContent = CursorUtil.getColumnIndexOrThrow(_cursor, "content");
          final int _cursorIndexOfType = CursorUtil.getColumnIndexOrThrow(_cursor, "type");
          final int _cursorIndexOfTimestamp = CursorUtil.getColumnIndexOrThrow(_cursor, "timestamp");
          final int _cursorIndexOfHasEncryption = CursorUtil.getColumnIndexOrThrow(_cursor, "hasEncryption");
          final int _cursorIndexOfLogoFilePath = CursorUtil.getColumnIndexOrThrow(_cursor, "logoFilePath");
          final int _cursorIndexOfStyleJson = CursorUtil.getColumnIndexOrThrow(_cursor, "styleJson");
          final List<GenerateHistoryEntity> _result = new ArrayList<GenerateHistoryEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final GenerateHistoryEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpContent;
            if (_cursor.isNull(_cursorIndexOfContent)) {
              _tmpContent = null;
            } else {
              _tmpContent = _cursor.getString(_cursorIndexOfContent);
            }
            final String _tmpType;
            if (_cursor.isNull(_cursorIndexOfType)) {
              _tmpType = null;
            } else {
              _tmpType = _cursor.getString(_cursorIndexOfType);
            }
            final long _tmpTimestamp;
            _tmpTimestamp = _cursor.getLong(_cursorIndexOfTimestamp);
            final boolean _tmpHasEncryption;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfHasEncryption);
            _tmpHasEncryption = _tmp != 0;
            final String _tmpLogoFilePath;
            if (_cursor.isNull(_cursorIndexOfLogoFilePath)) {
              _tmpLogoFilePath = null;
            } else {
              _tmpLogoFilePath = _cursor.getString(_cursorIndexOfLogoFilePath);
            }
            final String _tmpStyleJson;
            if (_cursor.isNull(_cursorIndexOfStyleJson)) {
              _tmpStyleJson = null;
            } else {
              _tmpStyleJson = _cursor.getString(_cursorIndexOfStyleJson);
            }
            _item = new GenerateHistoryEntity(_tmpId,_tmpContent,_tmpType,_tmpTimestamp,_tmpHasEncryption,_tmpLogoFilePath,_tmpStyleJson);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Flow<List<GenerateHistoryEntity>> getLatest(final int limit) {
    final String _sql = "SELECT * FROM generate_history ORDER BY timestamp DESC LIMIT ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, limit);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"generate_history"}, new Callable<List<GenerateHistoryEntity>>() {
      @Override
      @NonNull
      public List<GenerateHistoryEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfContent = CursorUtil.getColumnIndexOrThrow(_cursor, "content");
          final int _cursorIndexOfType = CursorUtil.getColumnIndexOrThrow(_cursor, "type");
          final int _cursorIndexOfTimestamp = CursorUtil.getColumnIndexOrThrow(_cursor, "timestamp");
          final int _cursorIndexOfHasEncryption = CursorUtil.getColumnIndexOrThrow(_cursor, "hasEncryption");
          final int _cursorIndexOfLogoFilePath = CursorUtil.getColumnIndexOrThrow(_cursor, "logoFilePath");
          final int _cursorIndexOfStyleJson = CursorUtil.getColumnIndexOrThrow(_cursor, "styleJson");
          final List<GenerateHistoryEntity> _result = new ArrayList<GenerateHistoryEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final GenerateHistoryEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpContent;
            if (_cursor.isNull(_cursorIndexOfContent)) {
              _tmpContent = null;
            } else {
              _tmpContent = _cursor.getString(_cursorIndexOfContent);
            }
            final String _tmpType;
            if (_cursor.isNull(_cursorIndexOfType)) {
              _tmpType = null;
            } else {
              _tmpType = _cursor.getString(_cursorIndexOfType);
            }
            final long _tmpTimestamp;
            _tmpTimestamp = _cursor.getLong(_cursorIndexOfTimestamp);
            final boolean _tmpHasEncryption;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfHasEncryption);
            _tmpHasEncryption = _tmp != 0;
            final String _tmpLogoFilePath;
            if (_cursor.isNull(_cursorIndexOfLogoFilePath)) {
              _tmpLogoFilePath = null;
            } else {
              _tmpLogoFilePath = _cursor.getString(_cursorIndexOfLogoFilePath);
            }
            final String _tmpStyleJson;
            if (_cursor.isNull(_cursorIndexOfStyleJson)) {
              _tmpStyleJson = null;
            } else {
              _tmpStyleJson = _cursor.getString(_cursorIndexOfStyleJson);
            }
            _item = new GenerateHistoryEntity(_tmpId,_tmpContent,_tmpType,_tmpTimestamp,_tmpHasEncryption,_tmpLogoFilePath,_tmpStyleJson);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Object exists(final String content, final Continuation<? super Boolean> $completion) {
    final String _sql = "SELECT COUNT(*) > 0 FROM generate_history WHERE content = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    if (content == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindString(_argIndex, content);
    }
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<Boolean>() {
      @Override
      @NonNull
      public Boolean call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final Boolean _result;
          if (_cursor.moveToFirst()) {
            final Integer _tmp;
            if (_cursor.isNull(0)) {
              _tmp = null;
            } else {
              _tmp = _cursor.getInt(0);
            }
            _result = _tmp == null ? null : _tmp != 0;
          } else {
            _result = null;
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @Override
  public Object getIdForContent(final String content,
      final Continuation<? super Long> $completion) {
    final String _sql = "SELECT id FROM generate_history WHERE content = ? LIMIT 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    if (content == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindString(_argIndex, content);
    }
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<Long>() {
      @Override
      @Nullable
      public Long call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final Long _result;
          if (_cursor.moveToFirst()) {
            if (_cursor.isNull(0)) {
              _result = null;
            } else {
              _result = _cursor.getLong(0);
            }
          } else {
            _result = null;
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
