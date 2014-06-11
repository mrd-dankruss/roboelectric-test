package com.mrdexpress.paperless.db;

/**
 * Created by hannobean on 2014/03/27.
 */
public class HannoSnippets {

    //Starting the Token Call First.
        /*
        aq.ajax(ServerInterface.getInstance(getApplicationContext()).getTokenUrl(), JSONObject.class, new AjaxCallback<JSONObject>() {
            public void callback(String url, JSONObject jObject, AjaxStatus status) {
                String Token = null;
                try {
                    if (jObject.has("response")) {
                        Token = jObject.getJSONObject("response").getJSONObject("auth").getString("token");

                    } else if (jObject.has("error")) {
                        Token = jObject.toString();
                    }
                } catch (JSONException e) {
                    Log.e("MRD-EX", "FIX THIS : " + e.getMessage());
                }
                Device.getInstance().setToken(Token);

                aq.ajax(ServerInterface.getInstance(getApplicationContext()).getUsersURL(), JSONObject.class, new AjaxCallback<JSONObject>() {
                    public void callback(String url, JSONObject json, AjaxStatus status) {
                        try {
                            if (json != null) {
                                //Generate Users Data
                                Users.getInstance().setUsers(json.toString());
                            }
                        } catch (Exception e) {
                            Log.e("MRD-EX", "FIX THIS : " + e.getMessage());
                        }
                        person_item_list = Users.getInstance().driversList;
                        UserAutoCompleteAdapter adapter = new UserAutoCompleteAdapter(getApplicationContext(),
                                person_item_list);

                        // Set the adapter
                        holder.text_name.setAdapter(adapter);
                        holder.text_name.setThreshold(1);

                        holder.text_name.setOnItemClickListener(new OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                                selected_user = ((Users.UserData) holder.text_name.getAdapter().getItem(position));
                                Users.getInstance().setActiveDriverIndex(position);
                                holder.text_name.setText(selected_user.getFullName());
                                holder.text_password.requestFocus();
                                //InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                                //imm.hideSoftInputFromWindow(holder.text_name.getWindowToken(), 0);
                            }
                        });
                    }
                });
            }
        });   */

}
