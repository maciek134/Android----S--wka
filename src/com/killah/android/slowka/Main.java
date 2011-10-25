package com.killah.android.slowka;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import android.app.Activity;
import android.app.Dialog;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Spinner;
import android.widget.CheckBox;

public class Main extends Activity {
	public boolean reverseWords = false;
	public String username = "";
	public String password = "";
	public boolean isLoggedIn = false;

	public List<String> items = new ArrayList<String>();
	public ListView LvItems = null;
	
	public List<String> questions = new ArrayList<String>();
	public List<String> fornext = new ArrayList<String>();
	public int wordcount = 0;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.main);

        final Spinner SpLists = (Spinner) findViewById(R.id.spinner1);
        final Button BtLogin = (Button) findViewById(R.id.button2);
        BtLogin.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if (!isLoggedIn) {
					final Dialog d = new Dialog(Main.this);
					d.setContentView(R.layout.login);
					d.setCancelable(true);
					d.setTitle("Zaloguj");
					
					SharedPreferences sp = getPreferences(MODE_PRIVATE);
					((EditText) d.findViewById(R.id.editText1)).setText(sp.getString("rememberedUsername", ""));
					
					d.findViewById(R.id.button1).setOnClickListener(new View.OnClickListener() {
						
						@Override
						public void onClick(View v) {
						
							try {
								EditText EdUser = (EditText) d.findViewById(R.id.editText1);
								EditText EdPasw = (EditText) d.findViewById(R.id.editText2);
								SharedPreferences sp = getPreferences(MODE_PRIVATE);
								SharedPreferences.Editor editor = sp.edit();
								editor.putString("rememberedUsername", EdUser.getText().toString());
								editor.commit();
								
								URL url = new URL("http://apps.killahforge.com/slowka?cmd=login&username=" + EdUser.getText() + "&password=" + EdPasw.getText());
								URLConnection conn = url.openConnection();
								
								BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
								
								String line = "";
								if ((line = rd.readLine()) != null) {
									if (line.equals("ok")) {
										d.dismiss();
										username = EdUser.getText().toString();
										password = EdPasw.getText().toString();
										((TextView) Main.this.findViewById(R.id.textView4)).setText(username);
										isLoggedIn = true;
										BtLogin.setText(getResources().getString(R.string.button_logout));
										((Button) Main.this.findViewById(R.id.button3)).setVisibility(View.VISIBLE);
									} else {
										Toast.makeText(getApplicationContext(), "Logowanie nieudane.", Toast.LENGTH_SHORT).show();
									}
								}
							} catch (Exception e) {
								Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_LONG).show();
							}
						}
					});
					
					d.show();

				} else {
					username = "";
					password = "";
					((TextView) Main.this.findViewById(R.id.textView4)).setText(getResources().getString(R.string.text_notlogged));
					isLoggedIn = false;
					BtLogin.setText(getResources().getString(R.string.button_login));
					((Button) Main.this.findViewById(R.id.button3)).setVisibility(View.INVISIBLE);
				}
			}
		});
        
        // START
        ((Button) findViewById(R.id.button1)).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if (SpLists.getSelectedItem() != null) {
					final Dialog s = new Dialog(Main.this);
					s.setTitle("Start");
					s.setCancelable(true);
					s.setContentView(R.layout.start);
					
					s.findViewById(R.id.button1).setOnClickListener(new View.OnClickListener() {
						
						@Override
						public void onClick(View v) {
							fornext.clear();
							questions.clear();
							boolean allLists = !((RadioButton) s.findViewById(R.id.radio0)).isChecked();
							reverseWords = ((CheckBox) s.findViewById(R.id.checkBox1)).isChecked();
							if (!allLists) {
								SharedPreferences sp = getPreferences(MODE_PRIVATE);
								
								String list = sp.getString(SpLists.getSelectedItem().toString(), "");
								if (!list.equals("")) {
									String lists[] = list.split(",");
									if (lists.length > 0) {
										for (int i = 0; i < lists.length; i++) questions.add(lists[i]);
										wordcount = lists.length;
										s.dismiss();
										startQuestions(SpLists.getSelectedItem().toString());
									} else {
										Toast.makeText(getApplicationContext(), "Lista jest pusta", Toast.LENGTH_SHORT);
									}
								} else {
									Toast.makeText(getApplicationContext(), "Lista jest pusta", Toast.LENGTH_SHORT);
								}
							} else {
									SharedPreferences sp = getPreferences(MODE_PRIVATE);
									String lnames = sp.getString("ListNames", "");
									String nlists[] = lnames.split(",");
									for (int i = 0; i < nlists.length; i++) {									
										String list = sp.getString(nlists[i], "");
										if (!list.equals("")) {
											String lists[] = list.split(",");
											if (lists.length > 0) {
												for (int j = 0; j < lists.length; j++) questions.add(lists[j]);
												wordcount += lists.length;
											}
										}
									}
									s.dismiss();
									startQuestions("Wszystkie listy");
							}
						}
					});
					
					s.show();
				} else {
					Toast.makeText(getApplicationContext(), "Wybierz jak¹œ listê!", Toast.LENGTH_SHORT).show();
				}
			}
		});
        
        // +
        ((Button) findViewById(R.id.button5)).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				final Dialog a = new Dialog(Main.this);
				a.setContentView(R.layout.addlist);
				a.setCancelable(true);
				a.setTitle("Dodaj listê");
				
				LvItems = (ListView) a.findViewById(R.id.listView1);
				
				((Button) a.findViewById(R.id.button1)).setOnClickListener(new View.OnClickListener() {
					
					@Override
					public void onClick(View v) {
						try {
							EditText Ed1 = (EditText) a.findViewById(R.id.editText2);
							EditText Ed2 = (EditText) a.findViewById(R.id.editText3);
							if ((Ed1.getText().toString().equals("")) || (Ed2.getText().toString().equals(""))) throw new Exception();
							items.add(Ed1.getText().toString() + ":" + Ed2.getText().toString());
							Ed1.setText("");
							Ed2.setText("");
							LvItems.setAdapter(new ListWordsAdapter(Main.this, items));
						} catch (Exception e) {
							Toast.makeText(getApplicationContext(), "Pola nie mog¹ byæ puste!", Toast.LENGTH_SHORT).show();
							return;
						}
					}
				});
				
				((Button) a.findViewById(R.id.button2)).setOnClickListener(new View.OnClickListener() {
					
					@Override
					public void onClick(View v) {
						if (((EditText) a.findViewById(R.id.editText1)).getText().equals("")) {
							Toast.makeText(getApplicationContext(), "Podaj nazwê listy", Toast.LENGTH_SHORT).show();
							return;
						}
						SharedPreferences sp = getPreferences(MODE_PRIVATE);
						SharedPreferences.Editor editor = sp.edit();
						
						String names = sp.getString("ListNames", "");

						if (names.contains(((EditText) a.findViewById(R.id.editText1)).getText())) return;
						if (names.equals(""))
							editor.putString("ListNames", names + "" + ((EditText) a.findViewById(R.id.editText1)).getText());
						else
							editor.putString("ListNames", names + "," + ((EditText) a.findViewById(R.id.editText1)).getText());
					
						editor.putString(((EditText) a.findViewById(R.id.editText1)).getText().toString(), listToString());
						
						editor.commit();
						items.clear();
						a.dismiss();
					}
				});
				
				a.show();
			}
		});
        
        ((Button) findViewById(R.id.button3)).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				final Dialog s = new Dialog(Main.this);
				s.setTitle("Synchronizuj");
				s.setCancelable(true);
				s.setContentView(R.layout.sync);
				
				s.findViewById(R.id.button1).setOnClickListener(new View.OnClickListener() {
					
					@Override
					public void onClick(View v) {
					
						try {
							URL url = new URL("http://apps.killahforge.com/slowka?cmd=getlistnames&username=" + username);
							URLConnection conn = url.openConnection();
							
							BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
							
							String line = "";
							while ((line = rd.readLine()) != null) {
								SharedPreferences sp = getPreferences(MODE_PRIVATE);
								SharedPreferences.Editor editor = sp.edit();
								line = line.replaceAll("\"", "");
								String names = sp.getString("ListNames", "");
								if (names.contains(line)) continue;
								if (names.equals(""))
									editor.putString("ListNames", names + "" + line);
								else
									editor.putString("ListNames", names + "," + line);
							
								try {
									URL lurl = new URL("http://apps.killahforge.com/slowka?cmd=getlist&name=" + URLEncoder.encode(line) + "&username=" + username);
									URLConnection lconn = lurl.openConnection();
									
									BufferedReader lrd = new BufferedReader(new InputStreamReader(lconn.getInputStream()));
									
									String lc = "";
									if ((lc = lrd.readLine()) != null) {
										editor.putString(line, lc);
										editor.commit();
									}
								} catch (Exception e) {
									Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_LONG).show();
								}
							}
						} catch (Exception e) {
							Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_LONG).show();
						} finally {
							s.dismiss();
						}
					}
				});
				
				s.show();
			}
		});
        
        SpLists.setOnTouchListener(new View.OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				try {
					SharedPreferences sp = getPreferences(MODE_PRIVATE);
					String lnames = sp.getString("ListNames", "");
					if (lnames.equals("")) { 
						String[] xlists = {};
						SpLists.setAdapter(new ArrayAdapter(Main.this, android.R.layout.simple_dropdown_item_1line, xlists));
						return true; 
					}
					String lists[] = lnames.split(",");
					if (lists.length > 0)
						SpLists.setAdapter(new ArrayAdapter(Main.this, android.R.layout.simple_dropdown_item_1line, lists));
					if (lists.length == 1) return true;
					
					return false;
				} catch (Exception e) {
					String[] xlists = {};
					SpLists.setAdapter(new ArrayAdapter(Main.this, android.R.layout.simple_dropdown_item_1line, xlists));
					return true;
				}
			}
		});
        
        // -
        ((Button) findViewById(R.id.button4)).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				String name = "";
				try {
					name = SpLists.getSelectedItem().toString();
					if (name.equals("")) return;
				} catch (Exception e) {
					return;
				}
				SharedPreferences sp = getPreferences(MODE_PRIVATE);
				SharedPreferences.Editor editor = sp.edit();
				editor.remove(name);
				String lnamesb = sp.getString("ListNames", "");
				lnamesb = lnamesb.replaceAll("," + name, "");
				lnamesb = lnamesb.replaceAll(name, "");
				if (lnamesb.startsWith(",")) lnamesb = lnamesb.substring(1);
				editor.putString("ListNames", lnamesb);
				editor.commit();
				

				sp = getPreferences(MODE_PRIVATE);
				editor = sp.edit();
				String lnames = sp.getString("ListNames", "");
				if (lnames.equals("")) {
					String[] xlists = {};
					SpLists.setAdapter(new ArrayAdapter(Main.this, android.R.layout.simple_dropdown_item_1line, xlists));
					return;
				} else {
					String lists[] = lnames.split(",");
					if (lists.length > 0)
						SpLists.setAdapter(new ArrayAdapter(Main.this, android.R.layout.simple_dropdown_item_1line, lists));
					else {
						String[] xlists = {};
						SpLists.setAdapter(new ArrayAdapter(Main.this, android.R.layout.simple_dropdown_item_1line, xlists));
						return;
					}
				}
			}
		});
    }
    
    public void removeItem(int n) {
    	items.remove(n);
    	LvItems.setAdapter(new ListWordsAdapter(Main.this, items));
    }
    
    public String listToString() {
    	String r = "";
    	for (int i = 0; i < items.size(); i++) {
    		r += items.get(i);
    		if (i < items.size() - 1) r += ",";
    	}
    	return r;
    }
    
    public String changeEntities(String t) {
    	t = t.replaceAll("\\&#261;", "¹");
    	t = t.replaceAll("\\&#260;", "¥");
    	t = t.replaceAll("\\&#263;", "æ");
    	t = t.replaceAll("\\&#262;", "Æ");
    	t = t.replaceAll("\\&#281;", "ê");
    	t = t.replaceAll("\\&#280;", "Ê");
    	t = t.replaceAll("\\&#322;", "³");
    	t = t.replaceAll("\\&#321;", "£");
    	t = t.replaceAll("\\&#324;", "ñ");
    	t = t.replaceAll("\\&#323;", "Ñ");
    	t = t.replaceAll("\\&#243;", "ó");
    	t = t.replaceAll("\\&#211;", "Ó");
    	t = t.replaceAll("\\&#347;", "œ");
    	t = t.replaceAll("\\&#346;", "Œ");
    	t = t.replaceAll("\\&#380;", "¿");
    	t = t.replaceAll("\\&#379;", "¯");
    	t = t.replaceAll("\\&#378;", "Ÿ");
    	t = t.replaceAll("\\&#377;", "");
    	return t;
    }
    
    public void endTurn(final String name) {
    	final Dialog e = new Dialog(this);
    	e.setTitle("Runda zakoñczona");
    	e.setCancelable(false);
    	e.setContentView(R.layout.endturn);
    	
    	((TextView) e.findViewById(R.id.textView2)).setText(Integer.valueOf(wordcount - fornext.size()).toString() + "/" + Integer.valueOf(wordcount).toString());
    	
    	((Button) e.findViewById(R.id.button2)).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				e.dismiss();
				questions.clear();
				questions.addAll(fornext);
				fornext.clear();
				startQuestions(name);
			}
		});
    	
    	((Button) e.findViewById(R.id.button1)).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				e.dismiss();
				SharedPreferences sp = getPreferences(MODE_PRIVATE);
				
				String list = sp.getString(name, "");
				if (!list.equals("")) {
					String lists[] = list.split(",");
					if (lists.length > 0) {
						fornext.clear();
						questions.clear();
						for (int i = 0; i < lists.length; i++) questions.add(lists[i]);
						wordcount = lists.length;
						e.dismiss();
						startQuestions(name);
					}
				}
			}
		});
    	
    	((Button) e.findViewById(R.id.button3)).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				fornext.clear();
				questions.clear();
				e.dismiss();
			}
		});
    	
    	e.show();
    }
    
    public void startQuestions(final String name) {
    	final Dialog q = new Dialog(this);
    	q.setTitle(name);
    	q.setCancelable(false);
    	q.setContentView(R.layout.question);
    	
    	if (questions.size() <= 0) {
    		endTurn(name);
    		return;
    	}
   
    	final int qnumber = (questions.size() > 1) ? new Random().nextInt(questions.size() - 1): 0; 
    	final int wnumber = (reverseWords) ? 1 : 0;
    	final int wnumbers = (reverseWords) ? 0 : 1;
    	
    	((TextView) q.findViewById(R.id.textView1)).setText(changeEntities(questions.get(qnumber).split(":")[wnumber]));
    	
    	((Button) q.findViewById(R.id.button1)).setOnTouchListener(new View.OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_UP) {
					((TextView) q.findViewById(R.id.textView2)).setText("*******");
				} else {
					((TextView) q.findViewById(R.id.textView2)).setText(changeEntities(questions.get(qnumber).split(":")[wnumbers]));
				}
				return false;
			}
		});
    	
    	((Button) q.findViewById(R.id.button2)).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				questions.remove(qnumber);
				q.dismiss();
				startQuestions(name);
			}
		});
    	
    	((Button) q.findViewById(R.id.button3)).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				fornext.add(questions.get(qnumber));
				questions.remove(qnumber);
				q.dismiss();
				startQuestions(name);
			}
		});
    	
    	q.show();
    }
}