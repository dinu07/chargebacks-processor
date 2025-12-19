package com.chargebacks.processor.processor;

import com.chargebacks.processor.model.Chargeback;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

@Component
public class ChargebackItemProcessor implements ItemProcessor<Chargeback, Chargeback> {

    @Override
    public Chargeback process(Chargeback chargeback) throws Exception {
        // Pass-through processor - can be extended for data transformation if needed
        return chargeback;
    }
}

