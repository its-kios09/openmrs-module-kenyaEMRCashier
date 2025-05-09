package org.openmrs.module.kenyaemr.cashier.rest.controller;

import org.openmrs.api.context.Context;
import org.openmrs.module.kenyaemr.cashier.api.IBillableItemsService;
import org.openmrs.module.kenyaemr.cashier.api.model.BillableService;
import org.openmrs.module.kenyaemr.cashier.rest.controller.base.CashierResourceController;
import org.openmrs.module.kenyaemr.cashier.rest.restmapper.BillableServiceMapper;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.v1_0.controller.BaseRestController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.server.ResponseStatusException;

@Controller
@RequestMapping(value = "/rest/" + RestConstants.VERSION_1 + CashierResourceController.KENYAEMR_CASHIER_NAMESPACE + "/api")
public class CashierRestController extends BaseRestController {
    @RequestMapping(method = RequestMethod.POST, path = "/billable-service")
    @ResponseBody
    public Object get(@RequestBody BillableServiceMapper request) {
        // Update the associated billable service item if a UUID is present in the request.
        if (request.getUuid() != null) {
            System.out.println("Updating billable service item " + request.getName());
            BillableService billableService = request.billableServiceUpdateMapper(request);
            IBillableItemsService service = Context.getService(IBillableItemsService.class);
            service.save(billableService);
        } else {
            System.out.println("Saving a new service item " + request.getName());
            BillableService billableService = request.billableServiceMapper(request);
            IBillableItemsService service = Context.getService(IBillableItemsService.class);
            service.save(billableService);
        }
        return true;
    } 

    /**
     * Deletes a billable service
     * 
     * @param uuid the uuid of the billable service to delete
     * @return ResponseEntity with 204 status on successful deletion
     * @throws ResponseStatusException if billable service not found
     */
    @RequestMapping(method = RequestMethod.DELETE, path = "/billable-service/{uuid}")
    @ResponseBody
    public ResponseEntity<Object> delete(@PathVariable String uuid) {
        IBillableItemsService service = Context.getService(IBillableItemsService.class);
        try {
            BillableService billableService = service.getByUuid(uuid);
            if (billableService == null) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Billable service not found");
            }
            service.voidEntity(billableService, "Deleted via REST API");
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to delete billable service", e);
        }
    }
}
