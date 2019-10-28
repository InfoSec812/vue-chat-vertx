// import something here
import VertxEventBus from 'vue-vertx3-eventbus-client'


// "async" is optional
export default async ({ Vue }) => {
  Vue.use(VertxEventBus, {
    path: '/eventbus',
    port: window.location.port,
    options: {
      transports: [ // whitelist "long polling" and "websocket" Sock JS transports
        'xhr-polling',
        'websocket',
      ],
      vertxbus_reconnect_attempts_max: Infinity, // Max reconnect attempts
      vertxbus_reconnect_delay_min: 1000, // Initial delay (in ms) before first reconnect attempt
      vertxbus_reconnect_delay_max: 10000, // Max delay (in ms) between reconnect attempts
      vertxbus_reconnect_exponent: 2, // Exponential backoff factor
      vertxbus_randomization_factor: 0.5 // Randomization factor between 0 and 1
    }
  });
}
