package com.ahmedkh.kitchen.mapper;

import com.ahmedkh.kitchen.dto.response.KitchenStationResponse;
import com.ahmedkh.kitchen.dto.response.KitchenTicketItemResponse;
import com.ahmedkh.kitchen.dto.response.KitchenTicketResponse;
import com.ahmedkh.kitchen.entity.KitchenStation;
import com.ahmedkh.kitchen.entity.KitchenTicket;
import com.ahmedkh.kitchen.entity.KitchenTicketItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface KitchenMapper {

    @Mapping(target = "status", source = "status")
    @Mapping(target = "items", source = "items")
    KitchenTicketResponse toTicketResponse(KitchenTicket entity);

    @Mapping(target = "station", source = "station")
    KitchenTicketItemResponse toTicketItemResponse(KitchenTicketItem entity);

    @Mapping(target = "stationType", source = "stationType")
    KitchenStationResponse toStationResponse(KitchenStation entity);
}
