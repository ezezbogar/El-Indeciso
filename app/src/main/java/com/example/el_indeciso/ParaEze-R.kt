package com.example.el_indeciso

import android.app.AlertDialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView

// CONSIDERACIONES
// la variable context es la que te pase como parametro en el constructor de Game
// el llamado a getString(...) no me fije como arreglarlo, pero supuse que vos ya sabes como hacerlo ;)
// sentite libre de tocar lo que quieras, total sos vos el que lo tiene que usar

class ParaEze {

    // estos tienen que ser atributos de una clase
    lateinit var dialogBuilder: AlertDialog.Builder
    lateinit var alertDialog: AlertDialog


    // --------------- ESTOS 3 METODOS HACEN LA MAGIA, SON LOS QUE TENES QUE LLAMAR -------------//

    // Muestra el PopUp que indica que la ronda fue completada.
    // Recibe la cantidad de vidas del jugador, la ronda que se acaba de terminar y la cantidad
    // de rondas totales.
    private fun showCompletedRoundPopUp(lives:Int, round:Int, total_rounds: Int) {
        dialogBuilder = AlertDialog.Builder(context)
        val pop_up_layout: View =  LayoutInflater.from(context).inflate(R.layout.completed_round_pop_up, null)

        setCompletedRoundPopUpData(pop_up_layout, lives, round, total_rounds)
        initializeCloseButton(pop_up_layout.findViewById(R.id.close_button))
        showPopUp(pop_up_layout)
    }


    // Muestra el PopUp que indica que un jugador tiró mal una carta
    // Recibe el nombre del jugador que tiró dicha carta, la carta que tiró y un map de las cartas
    // que descartó cada jugador.
    // El map es de la forma ("gonza" to ["1","2","5"], "eze" to ["69", "420"], "chuchu" to [])
    private fun showWrongDropPopUp(drop_responsible: String, card_dropped: String, discarded_cards: Map<String, List<String>>) {
        dialogBuilder = AlertDialog.Builder(context)
        val pop_up_layout: View = LayoutInflater.from(context).inflate(R.layout.drop_pop_up, null)

        setWrongDropPopUpData(pop_up_layout, drop_responsible, card_dropped, discarded_cards)
        initializeCloseButton(pop_up_layout.findViewById(R.id.close_button))
        showPopUp(pop_up_layout)
    }


    // Muestra el PopUp que aparece al inicio de la partida.
    // El jugador toca "COMENZAR" y queda bloqueando hasta que todos hayan hecho lo mismo.
    // Los parametros manejalos vos, le estoy pasando un handler pero capaz que vos tenes un handler como
    // atributo y podes borrar este parametro.
    // Además no tengo ni la más remota idea de cómo chequeas que estén todos ready, lo dejo a tu criterio
    // mi rey. De ultima sabes donde encontrarme ;)
    private fun showStartPopUp(room_code: String, handler: Handler) {
        dialogBuilder = AlertDialog.Builder(context)
        val pop_up_layout: View = LayoutInflater.from(context).inflate(R.layout.start_pop_up, null)

        setStartPopUpData(pop_up_layout, room_code, handler)
        initializeStartButton(pop_up_layout.findViewById(R.id.start_button), handler)
        showPopUp(pop_up_layout)
    }


    //-------------------------------------------------------------------------------------------//


    // Setea la información que muestra el PopUp de ronda completada.
    private fun setCompletedRoundPopUpData(pop_up_layout: View, lives:Int, round:Int, total_rounds: Int) {
        val reward = if (round % 2 == 0) "1 vida" else "-"
        val title: TextView = pop_up_layout.findViewById(R.id.completed_round_title)
        val content: TextView = pop_up_layout.findViewById(R.id.completed_round_content)

        title.text = getString(R.string.round_completed_title, round.toString())
        content.text = getString(R.string.round_completed_text, reward,
                                    lives.toString(), (total_rounds-round).toString())
    }

    // Setea la información que muestra el PopUp de cuando alguien tira el numero equivocado.
    // El subtitulo dice quién pifió, qué carta tiró y que perdieron una vida.
    // Después muestra una lista scrolleable con las cartas de cada jugador que fueron descartadas.
    private fun setWrongDropPopUpData(pop_up_layout: View,
                                      drop_responsible: String,
                                      card_dropped: String,
                                      discarded_cards: Map<String, List<String>>) {

        val discarded_cards_messages: LinearLayout = pop_up_layout.findViewById(R.id.discarded_cards_messages)

        val subtitle: TextView = pop_up_layout.findViewById(R.id.drop_responsible)
        subtitle.text = getString(R.string.drop_responsible, drop_responsible, card_dropped)

        for (player: Map.Entry<String, List<String>> in discarded_cards) {
            for (card: String in player.value) {
                val discarded_message_layout =  LayoutInflater.from(context).inflate(R.layout.drop_pop_up_line, discarded_cards_messages, false)

                val discarded_message: TextView = discarded_message_layout.findViewById(R.id.discarded_message)
                discarded_message.text = getString(R.string.discard_message, player.key, card)

                discarded_cards_messages.addView(discarded_message_layout)
            }
        }
    }


    // No te lo puse pero capaz quer necesites como parametro el map con los jugadores que armas a partir de firebase
    private fun setStartPopUpData(pop_up_layout: View, room_code: String, handler: Handler) {
        val players_state = mutableMapOf<String, Boolean>() // no estoy seguro de que con esto aca funcione, si es un atributo de clase seguro que sí
        val players_state_view = mutableMapOf<String, TextView>()
        val players_state_container: LinearLayout = pop_up_layout.findViewById(R.id.players_state)
        var everyone_is_ready = false

        val room_text: TextView = pop_up_layout.findViewById(R.id.room_code)
        room_text.text = getString(R.string.room_message, room_code)

//        Loop recursivo que chequea los que estan conectados y su estado
//        Te dejo la logica, pero como no se realmente como haces lo de firebase reestructuralo como te parezca mejor.
        val view_connections: Runnable = object : Runnable {
            override fun run() {
                for (player in players_leidos_de_firebase_xdxd) {
                    if (!players_state.contains(player)) {
                        val connected_player = layoutInflater.inflate(R.layout.players_pop_up_line, players_state_container, false)

                        val player_name: TextView = connected_player.findViewById(R.id.connected_player_name)
                        val player_state: TextView = connected_player.findViewById(R.id.connected_player_state)
                        player_name.text = player.name

                        players_state_container.addView(connected_player)
                        players_state.put(player.name, player.state o false)
                        players_state_view.put(player.name, player_state)

                    } else if (players_state[player.name] != player.state) {
                        players_state_view[player.name]?.text = getString(R.string.ready)
                        players_state_view[player.name]?.setTextColor((Color.parseColor("#056266")))
                    }
                }
                everyone_is_ready = !players_state.containsValue(false)
                if (!everyone_is_ready) {
                    handler.postDelayed(this, 50)
                }
            }
        }
        handler.post(view_connections)
    }


    private fun showPopUp(pop_up_layout: View) {
        // magia de inicializacion
        dialogBuilder.setView(pop_up_layout);
        alertDialog = dialogBuilder.create()
        alertDialog.window!!.attributes.windowAnimations = R.style.DialogAnimation
        alertDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        alertDialog.setCanceledOnTouchOutside(false)
        alertDialog.show()
    }

    private fun initializeCloseButton(button: Button) {
        button.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View?) {
                alertDialog.dismiss()
            }
        })
    }

    private fun initializeStartButton(button: Button, handler: Handler) {
        button.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View?) {
                var waiting = true
                button.isClickable = false
                button.setBackgroundResource(R.drawable.popup_button_pressed)
                button.setText(R.string.wait_button)
//                Acá va la logica que setea el flag "estoy_listo"
//                ...


//                Loop recursivo que chequea que esten todos conectados
//                val wait_connections: Runnable = object : Runnable {
//                    override fun run() {
//                        waiting = chequear que esten todos conectados
//                        if (waiting) {
//                            handler.postDelayed(this, 50)
//                        } else {
//                            alertDialog.dismiss()
//                        }
//                    }
//                }
//                handler.post(wait_connections)
            }
        })
    }
}