package com.example.sasha.singletask.settings.VariantsRecyclerView;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import com.example.sasha.singletask.R;
import com.example.sasha.singletask.helpers.Ui;

public class VariantsItemViewHolder extends RecyclerView.ViewHolder {

    private final EditText variantNameEditText;
    private final ImageButton deleteButton;
    private final VariantsDataSource dataSource;

    public VariantsItemViewHolder(View itemView, VariantsDataSource dataSource) {
        super(itemView);
        this.variantNameEditText = (EditText) itemView.findViewById(R.id.editTextVariantName);
        this.deleteButton = (ImageButton) itemView.findViewById(R.id.btnDeleteVariant);
        this.dataSource = dataSource;
    }

    public void bind(final VariantsItem variant) {
        variantNameEditText.setText(variant.getVariantName());
        variantNameEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    variant.setVariantName(variantNameEditText.getText().toString());
                    if (variantNameEditText.getText().toString().isEmpty()) {
                        Ui.run(new Runnable() {
                            @Override
                            public void run() {
                                dataSource.remove(variant);
                            }
                        });
                    }
                }
                variantNameEditText.setEnabled(false);
            }
        });
        variantNameEditText.setEnabled(false);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dataSource.remove(variant);
            }
        });
    }

    public void setFocusOnVariantName() {
        variantNameEditText.requestFocus();
    }

    public void setEditable() {
        variantNameEditText.setEnabled(true);
        variantNameEditText.requestFocus();
    }
}
