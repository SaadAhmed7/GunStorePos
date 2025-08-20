package com.project.posgunstore.Store.Repository;

import com.project.posgunstore.Store.Model.Store;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface StoreRepository extends JpaRepository<Store, UUID> {}