const functions = require("firebase-functions/v2");
const scheduler = require("firebase-functions/v2/scheduler");
const admin = require("firebase-admin");

admin.initializeApp();

functions.setGlobalOptions({ maxInstances: 10 });

exports.cleanupExpiredReservations = scheduler.onSchedule(
  "every 30 minutes",
  {
    timeZone: "Europe/Madrid"
  },
  async (event) => {
    const db = admin.firestore();
    const now = new Date();

    const snapshot = await db.collection("reservas").get();

    snapshot.forEach(async (doc) => {
      const data = doc.data();
      const fecha = data.fecha.toDate();

      if (fecha < now) {
        console.log(`Deleting expired reservation ${doc.id}`);
        const plazaId = data.plaza;

        await db.collection("reservas").doc(doc.id).delete();

        if (plazaId) {
          await db.collection("plazas").doc(plazaId).update({
            ocupada: false
          });
          console.log(`Plaza ${plazaId} liberada`);
        }
      }
    });

    console.log("Expired reservations cleanup done.");
    return null;
  }
);
