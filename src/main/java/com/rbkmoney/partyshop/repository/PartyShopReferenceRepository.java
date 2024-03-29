package com.rbkmoney.partyshop.repository;

import com.rbkmoney.partyshop.entity.PartyShopPK;
import com.rbkmoney.partyshop.entity.PartyShopReference;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PartyShopReferenceRepository extends JpaRepository<PartyShopReference, PartyShopPK> {

    @SuppressWarnings("checkstyle:AbbreviationAsWordInName")
    List<PartyShopReference> findByPartyShopPKPartyIdAndCategoryType(String partyId, String type);

    Optional<PartyShopReference> findByPartyShopPK(PartyShopPK partyShopPk);

}
