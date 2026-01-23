package com.web.model.repositories;

import java.sql.SQLException;
import java.util.List;

public interface GenericRepository <CLAZZ, KEY>{

    public void create(CLAZZ c) throws SQLException;
    public void update(CLAZZ c) throws SQLException;
    public CLAZZ read(KEY k) throws SQLException;
    public void delete(KEY k) throws SQLException;
    public List<CLAZZ> readAll() throws SQLException;

} 