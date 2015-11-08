package com.example.paginate.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.paginate.R;
import com.example.paginate.data.Person;

import java.util.List;

public class PersonAdapter extends BindableAdapter<Person> {

    private List<Person> data;

    public PersonAdapter(Context context, List<Person> data) {
        super(context);
        this.data = data;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Person getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View newView(LayoutInflater inflater, int position, ViewGroup container) {
        View view = LayoutInflater.from(container.getContext()).inflate(R.layout.person_list_item, container, false);
        PersonVH holder = new PersonVH(view);
        view.setTag(holder);
        return view;
    }

    @Override
    public void bindView(Person person, int position, View view) {
        PersonVH holder = (PersonVH) view.getTag();
        holder.tvFullName.setText(String.format("%s %s, %d", person.getFirstName(), person.getLastName(), person.getAge()));
    }

    public void add(List<Person> data) {
        this.data.addAll(data);
        notifyDataSetChanged();
    }

    public static class PersonVH {
        TextView tvFullName;

        public PersonVH(View view) {
            tvFullName = (TextView) view.findViewById(R.id.tv_full_name);
        }
    }

}