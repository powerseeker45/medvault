package com.example.medvault;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.medvault.R;
import com.example.medvault.HealthRecord;
import com.example.medvault.EncryptionUtil;

import java.util.List;

public class RecordAdapter extends BaseAdapter {

    Context context;
    List<HealthRecord> recordList;
    LayoutInflater inflater;

    public RecordAdapter(Context context, List<HealthRecord> recordList) {
        this.context = context;
        this.recordList = recordList;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return recordList.size();
    }

    @Override
    public Object getItem(int position) {
        return recordList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return recordList.get(position).id;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = inflater.inflate(R.layout.record_item, null);

        TextView title = convertView.findViewById(R.id.recordTitle);
        TextView notes = convertView.findViewById(R.id.recordNotes);
        TextView date = convertView.findViewById(R.id.recordDate);

        // Use already-decrypted data
        HealthRecord record = recordList.get(position);
        title.setText(record.title);
        notes.setText(record.notes);
        date.setText(record.date);

        return convertView;
    }

}

